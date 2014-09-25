package de.unistuttgart.vis.vita.analysis;

import java.nio.file.Path;

/**
 * Provides {@link AnalysisExecutor} objects that will perform the analysis of document
 */
public interface AnalysisExecutorFactory {
  /**
   * Provides the {@link AnalysisExecutor} that will perform the analysis of the given document
   * 
   * @param documetnId the id of the document being analyzed
   * @param documentPath the path to the document to analyze
   * @return the {@link AnalysisExecutor}
   */
  AnalysisExecutor createExecutor(String documentId, Path documentPath);
}
