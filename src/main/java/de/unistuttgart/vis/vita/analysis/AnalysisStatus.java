package de.unistuttgart.vis.vita.analysis;

/**
 * The phases an analysis goes through. Context can be a single module or a whole document.
 */
public enum AnalysisStatus {
  /**
   * Indicates that the analysis can be started
   */
  READY,

  /**
   * Indicates that the analysis has been started
   */
  RUNNING,

  /**
   * Indicates that the analysis has been cancelled by the owner (not by a module failure)
   */
  CANCELLED,

  /**
   * Indicates that the analysis has been stopped and did not succeed
   */
  FAILED,

  /**
   * Indicates that the analysis has been stopped and suceeded
   */
  FINISHED
}
