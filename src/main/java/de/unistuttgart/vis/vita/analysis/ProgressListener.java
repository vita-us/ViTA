package de.unistuttgart.vis.vita.analysis;

/**
 * A listener interface for observing the progress of a module.
 *
 */
public interface ProgressListener {

  /**
   * Informs the listener about the progress of a module
   * 
   * @param progress a value between 0 and 1
   */
  public void observeProgress(double progress);

}
