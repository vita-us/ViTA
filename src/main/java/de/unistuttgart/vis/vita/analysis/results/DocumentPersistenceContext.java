package de.unistuttgart.vis.vita.analysis.results;

/**
 * Provides everything one needs to know about a document to persist information about it
 */
public interface DocumentPersistenceContext {

  /**
   * Gets the id of the document being analyzed
   *
   * @return the document id
   */
  public String getDocumentId();
}
