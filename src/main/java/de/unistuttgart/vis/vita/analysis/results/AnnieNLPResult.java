package de.unistuttgart.vis.vita.analysis.results;

import de.unistuttgart.vis.vita.model.document.Chapter;

/**
 * The result of ANNIE NLP analysis
 */
public interface AnnieNLPResult {

  /**
   * Gets the ANNIE result for a single chapter
   *
   * @param chapter the chapter
   * @return the ANNIE results document
   */
  public gate.Document getDocumentForChapter(Chapter chapter);
}
