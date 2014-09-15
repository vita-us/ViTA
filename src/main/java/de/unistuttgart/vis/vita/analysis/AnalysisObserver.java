package de.unistuttgart.vis.vita.analysis;

public interface AnalysisObserver {
  /**
   * Is called when the analysis fails. At the time this method is called, the executor is in the
   * status {#link {@link AnalysisStatus#FAILED}.
   * 
   * @param executor the executor which failed
   */
  void onFail(AnalysisExecutor executor);

  /**
   * Is called when the analysis successfully finishes. At the time this method is called, the
   * executor is in the status {#link {@link AnalysisStatus#FINISHED}.
   * 
   * @param executor the executor which failed
   */
  void onFinish(AnalysisExecutor executor);
}
