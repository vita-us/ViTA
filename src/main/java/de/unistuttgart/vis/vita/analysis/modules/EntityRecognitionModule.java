/*
 * EntityRecognitionModule.java
 */

package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.modules.gate.NLPConstants;
import de.unistuttgart.vis.vita.analysis.results.BasicEntityCollection;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.analysis.results.NLPResult;
import de.unistuttgart.vis.vita.analysis.results.SentenceDetectionResult;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.document.Occurrence;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.AttributeType;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;
import de.unistuttgart.vis.vita.model.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gate.Annotation;
import gate.creole.ANNIEConstants;

/**
 * Searches for Entities in the document. Also merges entities and decides which name they should
 * use. Rare Entites will be filtered to improve the result.
 */
@AnalysisModule(dependencies = {ImportResult.class, NLPResult.class, SentenceDetectionResult.class,
    AnalysisParameters.class}, weight = 0.1)
public class EntityRecognitionModule extends Module<BasicEntityCollection> {

  private static final List<String>
      TYPES_PERSON = Arrays.asList(ANNIEConstants.PERSON_ANNOTATION_TYPE,
                                   NLPConstants.TYPE_PERSON_STANFORD);
  private Map<Integer, BasicEntity> idMap = new HashMap<>();
  private Set<BasicEntity> entities = new HashSet<>();
  private ImportResult importResult;
  private SentenceDetectionResult sentenceDetectionResult;
  private NLPResult nlpResult;
  private ProgressListener progressListener;
  private AnalysisParameters analysisParameters;

  @Override
  public BasicEntityCollection execute(ModuleResultProvider result,
      ProgressListener progressListener) throws Exception {
    importResult = result.getResultFor(ImportResult.class);
    nlpResult = result.getResultFor(NLPResult.class);
    sentenceDetectionResult = result.getResultFor(SentenceDetectionResult.class);
    analysisParameters = result.getResultFor(AnalysisParameters.class);
    this.progressListener = progressListener;

    startAnalysis();

    progressListener.observeProgress(1);
    mergeSameEntities();
    filterEntities();
    sortNames();
    return buildResult();
  }

  /**
   * Merges all entities that have the same name
   */
  private void mergeSameEntities() {
    EntityMerger merger = new EntityMerger();
    merger.addAll(entities);
    entities = new HashSet<>(merger.getResult());
  }

  /**
   * Takes the first name of the sorted set and sets it as the displayed name.
   */
  private void sortNames() {
    for (BasicEntity entity : entities) {
      List<Attribute> bufList = new ArrayList<>(entity.getNameAttributes());

      Collections.sort(bufList, new Comparator<Attribute>() {
        @Override
        public int compare(Attribute o1, Attribute o2) {
          return (o1.getOccurrences().size() > o2.getOccurrences().size() ? -1 : (o1
              .getOccurrences().size() == o2.getOccurrences().size() ? 0 : 1));
        }
      });

      entity.setNameAttributes(new HashSet<>(bufList));
      entity.setDisplayName(entity.getNameAttributes().iterator().next().getContent());
    }
  }

  /**
   * Removes those entities which has only one or less occurrences. Reduces the
   * chance from getting wrong entities based on NLP failure.
   */
  private void filterEntities() {
    Set<BasicEntity> entityToRemove = new HashSet<>();

    for (BasicEntity entity : entities) {
      if (entity.getOccurrences().size() <= 1) {
        entityToRemove.add(entity);
      }
    }

    entities.removeAll(entityToRemove);
  }

  /**
   * Starts the analysis by going through all annotations and create or update
   * the necessary entities.
   */
  private void startAnalysis() {
    double currentProgress;
    List<DocumentPart> documentParts = importResult.getParts();
    int currentPart = 0;
    int currentChapter = 0;
    int currentAnnotation = 0;
    double partFactor = 1.0 / documentParts.size();

    for (DocumentPart part : documentParts) {
      List<Chapter> chapters = part.getChapters();
      double chapterFactor = 1. / chapters.size();

      for (Chapter chapter : chapters) {
        Set<Annotation> annotations =
            nlpResult.getAnnotationsForChapter(chapter);

        for (Annotation annieAnnotation : annotations) {
          String firstChar= "" + getAnnotatedText(chapter.getText(), annieAnnotation).charAt(0);
          
          
          //Regex to filter wrong names with start char a-z or 0-9 
          if (analysisParameters.getStopEntityFilter()) { 
            if (firstChar.matches("^[a-z0-9\\W]")) {
              continue;
            }
          }
         
          
          createBasicEntity(annieAnnotation, chapter);
          double partProgress = ((double) currentPart) / documentParts.size();
          double chapterProgress = partFactor * ((double) currentChapter) / chapters.size();
          double annotProgress = chapterFactor * ((double) currentAnnotation) / annotations.size();
          currentProgress = partProgress + chapterProgress + annotProgress;
          progressListener.observeProgress(currentProgress);

          currentAnnotation++;
        }

        currentAnnotation = 1;
        currentChapter++;

        // Do not confuse annotation ids of different chapters
        idMap.clear();
      }

      currentChapter = 1;
      currentPart++;
    }
  }

  /**
   * Builds the result for the module containing the values of the map.
   *
   * @return Collection of all basic entities.
   */
  private BasicEntityCollection buildResult() {
    return new BasicEntityCollection() {
      @Override
      public Collection<BasicEntity> getEntities() {
        return entities;
      }
    };
  }

  /**
   * Creates a new entity out of the annotation in the given chapter.
   * If the entity already exists it will be updated with the matching position.
   * @param theAnnotation The annotation to work with.
   * @param chapter The chapter in which the annotation can be found.
   */
  private void createBasicEntity(Annotation theAnnotation, Chapter chapter) {
    BasicEntity entity = getExistingEntityForAnnotation(theAnnotation);
    String annotatedText = getAnnotatedText(chapter.getText(), theAnnotation);

    EntityType type = getEntityType(theAnnotation);

    if (entity == null) {
      entity = new BasicEntity();

      entity.setDisplayName(annotatedText);

      entity.setType(type);

      idMap.put(theAnnotation.getId(), entity);
      entities.add(entity);
    }
    Occurrence occurrence = getOccurences(theAnnotation, chapter);

    updateNameAttributes(entity, annotatedText, occurrence);

    entity.getOccurrences().add(occurrence);
  }

  private EntityType getEntityType(Annotation annotation) {
    if (TYPES_PERSON.contains(annotation.getType())) {
      return EntityType.PERSON;
    } else {
      return EntityType.PLACE;
    }
  }

  /**
   * Updates the name attributes for the given entity.
   * 
   * @param entity
   * @param entity The entity to be updated.
   * @param annotatedText The extracted name.
   *          The extracted name.
   * @param occurrence
   * @param occurrence The position of the name.
   */
  private void updateNameAttributes(BasicEntity entity, String annotatedText, Occurrence occurrence) {
    for (Attribute currentAttribute : entity.getNameAttributes()) {
      if (currentAttribute.getContent().equals(annotatedText)) {
        currentAttribute.getOccurrences().add(occurrence);
        return;
      }
    }

    Attribute newAttribute = new Attribute(AttributeType.NAME, annotatedText);

    newAttribute.getOccurrences().add(occurrence);
    entity.getNameAttributes().add(newAttribute);
  }

  /**
   * Searches for an already existing entity in the map.
   *
   * @param theAnnotation
   * @param theAnnotation The annotation to work with.
   * @return The existing entity if found else null.
   */
  @SuppressWarnings("unchecked")
  private BasicEntity getExistingEntityForAnnotation(Annotation theAnnotation) {
    List<Integer> matches = (List<Integer>) theAnnotation.getFeatures().get("matches");

    // If no matches are found
    if (matches == null) {
      return null;
    }

    for (int i : matches) {
      if (idMap.containsKey(i)) {
        return idMap.get(i);
      }
    }

    // TODO relations, ...

    return null;
  }

  /**
   * Creates an Occurrence for the given annotation. The Occurrence has the offset in the given
   * chapter.
   * 
   * @param theAnnotation
   *          The annotation to work with.
   * @param chapter
   *          The chapter in which the annotation can be found.
   * @return The Occurrence of the annotation.
   */
  private Occurrence getOccurences(Annotation theAnnotation, Chapter chapter) {
    return sentenceDetectionResult
        .createOccurrence(chapter, theAnnotation.getStartNode().getOffset().intValue(),
            theAnnotation.getEndNode().getOffset().intValue());
  }

  /**
   * Extract the annotated text.
   * 
   * @param text
   *          The text to cut out the annotated part.
   * @param theAnnotation
   *          The annotation to work with.
   * @return The annotated text.
   */
  private String getAnnotatedText(String text, Annotation theAnnotation) {
    int start = theAnnotation.getStartNode().getOffset().intValue();
    int end = theAnnotation.getEndNode().getOffset().intValue();
    return text.substring(start, end);
  }
}
