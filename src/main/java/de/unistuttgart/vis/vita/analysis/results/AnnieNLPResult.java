package de.unistuttgart.vis.vita.analysis.results;

import de.unistuttgart.vis.vita.model.document.Chapter;

import java.util.Collection;
import java.util.Set;

import gate.Annotation;

/**
 * The result of ANNIE NLP analysis
 */
public interface AnnieNLPResult {

  /**
   * Gets the ANNIE result for a single chapter.
   * The types for this annotations are <b>Person</b> and <b>Location</b>.
   * 
   * @param chapter The desired chapter.
   * @return the set with annotations for given chapter.
   */
  public Set<Annotation> getAnnotationsForChapter(Chapter chapter);


  /**
   * Gets the ANNIE result for a single chapter with the given types.<br> See {@link
   * gate.creole.ANNIEConstants}
   *
   * @param chapter The desired chapter.
   * @param type    Collection which contains the desired types. Have to be compatible with ANNIE
   *                Constants.
   * @return the set with annotations for given chapter.
   */
  public Set<Annotation> getAnnotationsForChapter(Chapter chapter, Collection<String> type);
}
