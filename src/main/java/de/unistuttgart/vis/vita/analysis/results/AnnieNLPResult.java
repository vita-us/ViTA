package de.unistuttgart.vis.vita.analysis.results;

import gate.Annotation;

import java.util.Set;

import de.unistuttgart.vis.vita.model.document.Chapter;

/**
 * The result of ANNIE NLP analysis
 */
public interface AnnieNLPResult {

  /**
   * Gets the ANNIE result for a single chapter
   * 
   * @param chapter The desired chapter.
   * @return the set with annotations for given chapter.
   */
  public Set<Annotation> getAnnotationsForChapter(Chapter chapter);
}
