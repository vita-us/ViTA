package de.unistuttgart.vis.vita.analysis;

/**
 * A listener interface for observing the progress of a module.
 *
 */
public interface ProgressListener {

  /**
   * TODO not really understand the meaning of this yet... Initiate the observation of the module
   * with given value.
   * 
   * @param progress The current progress of the module.
   */
  public void observeProgress(double progress);

}
