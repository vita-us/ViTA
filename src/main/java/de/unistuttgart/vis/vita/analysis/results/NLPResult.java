/*
 * NLPResult.java
 *
 */

package de.unistuttgart.vis.vita.analysis.results;

import de.unistuttgart.vis.vita.model.document.Chapter;

import java.util.Collection;
import java.util.Set;

import gate.Annotation;

/**
 *
 */
public interface NLPResult {

  /**
   * Gets all annotations found for the chapter.
   * The types for this annotations are <b>Person</b> and <b>Location</b>.
   *
   * @param chapter The desired chapter.
   * @return the set with annotations for given chapter.
   */
  public Set<Annotation> getAnnotationsForChapter(Chapter chapter);

  /**
   * Gets the result for a single chapter with the given types.<br> See {@link
   * gate.creole.ANNIEConstants}. But differs in the used tools.
   *
   * @param chapter The desired chapter.
   * @param type    Collection which contains the desired types. Have to be compatible with ANNIE
   *                Constants.
   * @return the set with annotations for given chapter.
   */
  public Set<Annotation> getAnnotationsForChapter(Chapter chapter, Collection<String> type);
}
