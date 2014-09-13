package de.unistuttgart.vis.vita.analysis.results;

import de.unistuttgart.vis.vita.model.document.Chapter;

import java.util.Set;

import gate.Annotation;

/**
 * The result of ANNIE NLP analysis
 */
public interface AnnieNLPResult {

  /**
   * @param chapter The desired chapter.
   * @return the set with annotations for given chapter.
   */
  public Set<Annotation> getAnnotationsForChapter(Chapter chapter);

  /**
   * Gets the ANNIE result for a single chapter
   *
   * @param chapter the chapter
   * @return the ANNIE results document
   */
  public gate.Document getDocumentForChapter(Chapter chapter);
}
