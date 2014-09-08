package de.unistuttgart.vis.vita.analysis;

import java.nio.file.Path;

/**
 * Provides {@link AnalysisExecutor} objects that will perform the analysis of document
 */
public interface AnalysisExecutorFactory {
  /**
   * Provides the {@link AnalysisExecutor} that will perform the analysis of the given document
   * 
   * @param documentPath the path to the document to analyze
   * @return the {@link AnalysisExecutor}
   */
  AnalysisExecutor createExecutor(Path documentPath);
}
