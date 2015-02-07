package de.unistuttgart.vis.vita.analysis.modules.gate;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.unistuttgart.vis.vita.analysis.results.NLPResult;
import de.unistuttgart.vis.vita.model.document.Chapter;

/**
 * Implements a NLP Result for {@link AbstractNLPModule}. Can be used for Annie NLP, Stanford NLP and Open NLP.
 */
public abstract class AbstractNLPResult implements NLPResult, AutoCloseable {

  protected Map<Chapter, Set<Annotation>> chapterToAnnotation;
  protected Map<Chapter, Document> chapterToDoc;
  private boolean isCleanedUp;

  /**
   * Builds a new NLP Result.
   * 
   * @param chapterToAnnotation - Contains all Annotations found for each chapter.
   * @param chapterToDoc - Contains the document for each chapter.
   */
  public AbstractNLPResult(Map<Chapter, Set<Annotation>> chapterToAnnotation,
      Map<Chapter, Document> chapterToDoc) {
    this.chapterToAnnotation = chapterToAnnotation;
    this.chapterToDoc = chapterToDoc;
  }

  @Override
  public Set<Annotation> getAnnotationsForChapter(Chapter chapter) {
    if (isCleanedUp) {
      throw new IllegalStateException("This result is already cleaned up");
    }
    if (!chapterToAnnotation.containsKey(chapter)) {
      throw new IllegalArgumentException("This chapter has not been analyzed");
    }

    return chapterToAnnotation.get(chapter);
  }

  @Override
  public Set<Annotation> getAnnotationsForChapter(Chapter chapter, Collection<String> type) {
    if (isCleanedUp) {
      throw new IllegalStateException("This result is already cleaned up");
    }
    if (!chapterToAnnotation.containsKey(chapter)) {
      throw new IllegalArgumentException("This chapter has not been analyzed");
    }

    Document document = chapterToDoc.get(chapter);

    AnnotationSet defaultAnnotSet = document.getAnnotations();
    Set<String> annotTypesRequired = new HashSet<>();

    for (String s : type) {
      annotTypesRequired.add(s);
    }

    return new HashSet<>(defaultAnnotSet.get(annotTypesRequired));
  }

  @Override
  public void close() throws Exception {
    if (isCleanedUp) {
      return;
    }

    for (Document document : chapterToDoc.values()) {
      document.cleanup();
    }

    isCleanedUp = true;
  }
}
