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
import de.unistuttgart.vis.vita.model.entity.BasicEntity;
import de.unistuttgart.vis.vita.model.entity.EntityType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import gate.Annotation;

/**
 *
 */
@AnalysisModule(dependencies = {ImportResult.class, AnnieNLPResult.class, StanfordNLPResult.class})
public class EntityRecognitionModule implements Module<BasicEntityCollection> {

  Map<String, BasicEntity> entityMap = new HashMap<>();
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
        return entityMap.values();
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
    String annotatedText = getAnnotatedText(chapter.getText(), theAnnotation);

    if (entityMap.containsKey(annotatedText)) {
      BasicEntity entity = entityMap.get(annotatedText);

      entity.getOccurences().add(getTextSpan(theAnnotation, chapter));
    } else {
      BasicEntity newEntity = new BasicEntity();
      newEntity.setDisplayName(annotatedText);
      EntityType type;

      if (theAnnotation.getType().equals("Person")) {
        type = EntityType.PERSON;
      } else {
        type = EntityType.PLACE;
      }

      newEntity.setType(type);
      newEntity.getOccurences().add(getTextSpan(theAnnotation, chapter));

      entityMap.put(newEntity.getDisplayName(), newEntity);

      // TODO relations, ...
    }
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
