package de.unistuttgart.vis.vita.analysis;

/**
 * The phases an analysis goes through. Context can be a single module or a whole document.
 */
public enum AnalysisStatus {
  NOT_STARTED, RUNNING, CANCELLED, FAILED, FINISHED
}
