/*
 * NLPResult.java
 *
 */

package de.unistuttgart.vis.vita.analysis.results;

import de.unistuttgart.vis.vita.model.document.Chapter;

import java.util.Set;

import gate.Annotation;

/**
 *
 */
public interface NLPResult {

  /**
   * Gets all annotations found for the chapter.
   *
   * @param chapter The desired chapter.
   * @return the set with annotations for given chapter.
   */
  public Set<Annotation> getAnnotationsForChapter(Chapter chapter);
}
