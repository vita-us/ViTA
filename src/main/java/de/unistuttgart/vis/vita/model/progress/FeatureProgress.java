package de.unistuttgart.vis.vita.model.progress;

/**
 * Represents the progress of the analysis for a specific feature of the software.
 * 
 * @author Marc Weise
 * @version 0.1 01.08.2014
 */
public class FeatureProgress {
  
  // attributes
  private double progress;
  private boolean isReady;
  
  /**
   * Creates a new instance of Feature Progress, setting the progress to 0 and ready flag to false.
   */
  public FeatureProgress() {
    this(0.0, false);
  }
  
  /**
   * Creates a new instance of FeatureProgress with the given progress value and ready flag.
   * 
   * @param pProgress - the progress value between 0.0 and 1.0
   * @param pReady - flag whether the feature this refers to is ready or not
   */
  public FeatureProgress(double pProgress, boolean pReady) {
    setProgress(pProgress);
    setReady(pReady);
  }

  /**
   * @return the progress
   */
  public double getProgress() {
    return progress;
  }

  /**
   * Sets the progress.
   *
   * @param progress - the progress to set
   */
  public void setProgress(double progress) {
    if (progress < 0.0 || progress > 1.0) {
      throw new IllegalArgumentException("progress value must be between 0 and 1");
    }
    
    this.progress = progress;
  }

  /**
   * @return the isReady
   */
  public boolean isReady() {
    return isReady;
  }

  /**
   * Sets the isReady.
   *
   * @param isReady - the isReady to set
   */
  public void setReady(boolean isReady) {
    this.isReady = isReady;
  }
  
}
