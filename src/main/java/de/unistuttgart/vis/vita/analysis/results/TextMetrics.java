package de.unistuttgart.vis.vita.analysis.results;

/**
 * Provides metric information about the document text
 */
public interface TextMetrics {
  /**
   * Gets the total number of words in the document
   * @return the number of words
   */
  public int getWordCount();
}
