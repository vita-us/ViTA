package de.unistuttgart.vis.vita.analysis;

import de.unistuttgart.vis.vita.model.document.Document;

import de.unistuttgart.vis.vita.model.document.AnalysisParameters;

/**
 * Provides {@link AnalysisExecutor} objects that will perform the analysis of document
 */
public interface AnalysisExecutorFactory {
  /**
   * Provides the {@link AnalysisExecutor} that will perform the analysis of the given document
   *
   * @param document The document with id and path.
   * @return the {@link AnalysisExecutor}
   */
  AnalysisExecutor createExecutor(Document document);
}
