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

import gate.Annotation;

/**
 *
 */
@AnalysisModule(dependencies = {ImportResult.class, AnnieNLPResult.class, StanfordNLPResult.class})
public class EntityRecognitionModule implements Module<BasicEntityCollection> {

  Map<Integer, BasicEntity> idMap = new HashMap<>();
  private ImportResult importResult;
  private AnnieNLPResult annieNLPResult;
  private StanfordNLPResult stanfordNLPResult;
  private ProgressListener progressListener;

  @Override
  public void observeProgress(double progress) {

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

  private BasicEntityCollection buildResult() {
    return new BasicEntityCollection() {
      @Override
      public Collection<BasicEntity> getEntities() {
        return idMap.values();
      }
    };
  }

  private Set<Annotation> filterEntityAnnotations(Set<Annotation> annotations) {
    Set<Annotation> extracted = new TreeSet<>();

    for (Annotation annotation : annotations) {
      if (annotation.getType().equals("Person") || annotation.getType().equals("Place")) {
        extracted.add(annotation);
      }
    }

    return extracted;
  }

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

  private TextSpan getTextSpan(Annotation theAnnotation, Chapter chapter) {
    TextPosition
        posStart = new TextPosition(chapter, theAnnotation.getStartNode().getOffset().intValue());
    TextPosition
        posEnd =
        new TextPosition(chapter, theAnnotation.getEndNode().getOffset().intValue());

    return new TextSpan(posStart, posEnd);
  }

  private String getAnnotatedText(String text, Annotation theAnnotation) {
    int start = theAnnotation.getStartNode().getOffset().intValue();
    int end = theAnnotation.getEndNode().getOffset().intValue();
    return text.substring(start, end);
  }
}
