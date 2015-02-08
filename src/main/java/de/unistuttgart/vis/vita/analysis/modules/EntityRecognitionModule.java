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
 * use. Rare Entities will be filtered to improve the result.
 */
@AnalysisModule(dependencies = {ImportResult.class, NLPResult.class, SentenceDetectionResult.class,
    AnalysisParameters.class}, weight = 0.1)
public class EntityRecognitionModule extends Module<BasicEntityCollection> {
  private static final int MINIMUM_ENTITY_OCCURRENCES = 2;
  private static final List<String> TYPES_PERSON = Arrays.asList(
      ANNIEConstants.PERSON_ANNOTATION_TYPE, NLPConstants.TYPE_PERSON_STANFORD);
  private Map<Integer, BasicEntity> idMap = new HashMap<>();
  private Set<BasicEntity> entities = new HashSet<>();
  private ImportResult importResult;
  private SentenceDetectionResult sentenceDetectionResult;
  private NLPResult nlpResult;
  private ProgressListener progressListener;
  private AnalysisParameters analysisParameters;


  /**
   * Compares Attributes depending on their number of Occurrences, lower number is smaller.
   */
  private static class AttributeComparator implements Comparator<Attribute> {
    @Override
    public int compare(Attribute o1, Attribute o2) {
      return o1.getOccurrences().size() > o2.getOccurrences().size() ? -1 : (o1.getOccurrences()
          .size() == o2.getOccurrences().size() ? 0 : 1);
    }
  }


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
   * Sorts the entity names depending on their number of occurrences and sets the first name (most
   * Occurrences) as the displayed name.
   */
  private void sortNames() {
    for (BasicEntity entity : entities) {
      List<Attribute> namesOfEntity = new ArrayList<>(entity.getNameAttributes());

      Collections.sort(namesOfEntity, new AttributeComparator());

      entity.setNameAttributes(new HashSet<>(namesOfEntity));
      entity.setDisplayName(entity.getNameAttributes().iterator().next().getContent());
    }
  }

  /**
   * Removes those entities which have only a predefined small number (or less) occurrences. Reduces
   * the chance from getting wrong entities based on NLP failure.
   */
  private void filterEntities() {
    Set<BasicEntity> entityToRemove = new HashSet<>();

    for (BasicEntity entity : entities) {
      if (entity.getOccurrences().size() < MINIMUM_ENTITY_OCCURRENCES) {
        entityToRemove.add(entity);
      }
    }

    entities.removeAll(entityToRemove);
  }

  /**
   * Starts the analysis by going through all annotations and create or update the necessary
   * entities. Manages the progress of the analysis.
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
      double partProgress = ((double) currentPart) / documentParts.size();

      for (Chapter chapter : chapters) {
        Set<Annotation> annotations = nlpResult.getAnnotationsForChapter(chapter);
        double chapterProgress = partFactor * ((double) currentChapter) / chapters.size();

        for (Annotation annieAnnotation : annotations) {
          double annotProgress = chapterFactor * ((double) currentAnnotation) / annotations.size();
         
          createBasicEntity(annieAnnotation, chapter);
          
          // compute and set progress
          currentProgress = partProgress + chapterProgress + annotProgress;
          progressListener.observeProgress(currentProgress);

          currentAnnotation++;
        }

        currentAnnotation = 0;
        currentChapter++;

        // Do not confuse annotation ids of different chapters
        idMap.clear();
      }

      currentChapter = 0;
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
   * Creates a new entity out of the annotation in the given chapter. If the entity already exists
   * it will be updated with the matching position. Will do nothing, when a filter for unlikely
   * names is activated and fits to this entity.
   * 
   * @param theAnnotation The annotation to work with.
   * @param chapter The chapter in which the annotation can be found.
   */
  private void createBasicEntity(Annotation theAnnotation, Chapter chapter) {
    String firstChar = "" + getAnnotatedText(chapter.getText(), theAnnotation).charAt(0);
    if (analysisParameters.getStopEntityFilter() && characterNotMatchesUnlikelyNameStart(firstChar)) {
      return;
    }

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

  /**
   * Checks if a name-start-character does not fit unlikely name-start-characters.
   * 
   * @param character -
   * @return true: The character seems to be a name-start-character. false: The character is most
   *         likely not a name-start-character.
   */
  private boolean characterNotMatchesUnlikelyNameStart(String character) {
    return character.matches("^[a-z0-9\\W]");
  }
  
  /**
   * For a given Annotation the type of the Entity will be returned.
   * 
   * @param annotation - the Annotation.
   * @return The type of an Entity, for example: Person or Place.
   */
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
   * @param annotatedText The extracted name. The extracted name.
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

    return null;
  }

  /**
   * Creates an Occurrence for the given annotation. The Occurrence has the offset in the given
   * chapter.
   * 
   * @param theAnnotation The annotation to work with.
   * @param chapter The chapter in which the annotation can be found.
   * @return The Occurrence of the annotation.
   */
  private Occurrence getOccurences(Annotation theAnnotation, Chapter chapter) {
    return sentenceDetectionResult.createOccurrence(chapter, theAnnotation.getStartNode()
        .getOffset().intValue(), theAnnotation.getEndNode().getOffset().intValue());
  }

  /**
   * Extract the annotated text.
   * 
   * @param text The text to cut out the annotated part.
   * @param theAnnotation The annotation to work with.
   * @return The annotated text.
   */
  private String getAnnotatedText(String text, Annotation theAnnotation) {
    int start = theAnnotation.getStartNode().getOffset().intValue();
    int end = theAnnotation.getEndNode().getOffset().intValue();
    return text.substring(start, end);
  }
}
