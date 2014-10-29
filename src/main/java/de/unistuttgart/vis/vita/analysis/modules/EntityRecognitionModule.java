/*
 * EntityRecognitionModule.java
 *
 */

package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.AnnieNLPResult;
import de.unistuttgart.vis.vita.analysis.results.BasicEntityCollection;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.analysis.results.StanfordNLPResult;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.AttributeType;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;
import de.unistuttgart.vis.vita.model.entity.EntityType;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nullable;

import gate.Annotation;

/**
 *
 */
@AnalysisModule(dependencies = {ImportResult.class, AnnieNLPResult.class, StanfordNLPResult.class})
public class EntityRecognitionModule implements Module<BasicEntityCollection> {

  private Map<Integer, BasicEntity> idMap = new HashMap<>();
  private ImportResult importResult;
  private AnnieNLPResult annieNLPResult;
  private StanfordNLPResult stanfordNLPResult;
  private ProgressListener progressListener;

  @Override
  public void observeProgress(double progress) {
    // TODO calculating the progress
  }

  @Override
  public BasicEntityCollection execute(ModuleResultProvider result,
                                       ProgressListener progressListener) throws Exception {
    importResult = result.getResultFor(ImportResult.class);
    annieNLPResult = result.getResultFor(AnnieNLPResult.class);
    stanfordNLPResult = result.getResultFor(StanfordNLPResult.class);
    this.progressListener = progressListener;

    startAnalysis();

    progressListener.observeProgress(1);
    return buildResult();
  }

  /**
   * Starts the analysis by going through all annotations and create or update the necessary
   * entities.
   */
  private void startAnalysis() {
    for (DocumentPart part : importResult.getParts()) {
      for (Chapter chapter : part.getChapters()) {
        for (Annotation annieAnnotation : filterEntityAnnotations(
            annieNLPResult.getAnnotationsForChapter(chapter))) {
          createBasicEntity(annieAnnotation, chapter);
        }
      }
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
        return idMap.values();
      }
    };
  }

  /**
   * Filters the gate annotations to only contain persons and locations.
   * @param annotations The gate annotation set.
   * @return The new filtered set.
   */
  private Set<Annotation> filterEntityAnnotations(Set<Annotation> annotations) {
    Set<Annotation> extracted = new TreeSet<>();

    for (Annotation annotation : annotations) {
      if (annotation.getType().equals("Person") || annotation.getType().equals("Location")) {
        extracted.add(annotation);
      }
    }

    return extracted;
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

    if (entity == null) {
      entity = new BasicEntity();

      entity.setDisplayName(annotatedText);
      EntityType type;

      if (theAnnotation.getType().equals("Person")) {
        type = EntityType.PERSON;
      } else {
        type = EntityType.PLACE;
      }

      entity.setType(type);

      idMap.put(theAnnotation.getId(), entity);
    }
    TextSpan textSpan = getTextSpan(theAnnotation, chapter);

    updateNameAttributes(entity, annotatedText, textSpan);

    entity.getOccurences().add(textSpan);

    // TODO relations, ...
  }

  /**
   * Updates the name attributes for the given entity.
   * @param entity The entity to be updated.
   * @param annotatedText The extracted name.
   * @param textSpan The position of the name.
   */
  private void updateNameAttributes(BasicEntity entity, String annotatedText, TextSpan textSpan) {
    for (Attribute currentAttribute : entity.getNameAttributes()) {
      if (currentAttribute.getContent().equals(annotatedText)) {
        currentAttribute.getOccurrences().add(textSpan);
        return;
      }
    }

    Attribute newAttribute = new Attribute(AttributeType.NAME, annotatedText);

    newAttribute.getOccurrences().add(textSpan);
    entity.getNameAttributes().add(newAttribute);
  }

  /**
   * Searches for an already existing entity in the map.
   *
   * @param theAnnotation The annotation to work with.
   * @return The existing entity if found else null.
   */
  @SuppressWarnings("unchecked")
  @Nullable
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
   * Creates a textpan for the given annotation. The textpan has the offset in the given chapter.
   * @param theAnnotation The annotation to work with.
   * @param chapter The chapter in which the annotation can be found.
   * @return The Textspan of the annotation.
   */
  private TextSpan getTextSpan(Annotation theAnnotation, Chapter chapter) {
    TextPosition
        posStart = new TextPosition(chapter, theAnnotation.getStartNode().getOffset().intValue());
    TextPosition
        posEnd =
        new TextPosition(chapter, theAnnotation.getEndNode().getOffset().intValue());

    return new TextSpan(posStart, posEnd);
  }

  /**
   * Extract the annotated text.
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
