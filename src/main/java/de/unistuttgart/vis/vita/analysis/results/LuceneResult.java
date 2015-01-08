package de.unistuttgart.vis.vita.analysis.results;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;

/**
 * The result of storing chapters in Lucene
 */
public interface LuceneResult {

  /**
   * Gets the IndexReader of the directory, in which the chapters of the current document are stored
   * @return indexReader of current document
   * @throws IOException
   */
  public IndexReader getIndexReader() throws IOException;
}
