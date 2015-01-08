package de.unistuttgart.vis.vita.model.progress;

import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlElement;

/**
 * Represents the progress of the analysis for a specific feature of the software.
 */
@Embeddable
public class FeatureProgress {

  // attributes
  private double progress;
  private boolean isReady;
  private boolean isFailed;

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
   * @param pReady    - flag whether the feature this refers to is ready or not
   */
  public FeatureProgress(double pProgress, boolean pReady) {
    setProgress(pProgress);
    setReady(pReady);
  }

  /**
   * Gets the progress of this feature
   *
   * @return a value between 0 (not started) and 1 (finished)
   */
  public double getProgress() {
    return progress;
  }

  /**
   * Sets the progress.
   *
   * @param progress a value between 0 (not started) and 1 (finished)
   */
  public void setProgress(double progress) {
    if (progress < 0.0 || progress > 1.0) {
      throw new IllegalArgumentException("progress value must be between 0 and 1");
    }

    this.progress = progress;
  }

  /**
   * Specifies whether the feature is completely analyzed and read for visualization
   *
   * @return true, if this feature is ready, false otherwise
   */
  @XmlElement(name = "isReady")
  public boolean isReady() {
    return isReady;
  }

  /**
   * Specifies whether the feature is completely analyzed and read for visualization.
   *
   * @param true, if this feature is ready, false otherwise
   */
  public void setReady(boolean isReady) {
    this.isReady = isReady;
  }

  /**
   * Specifies whether this feature has ended unsuccessfully
   * @return true, if the feature is failed
   */
  @XmlElement(name = "isFailed")
  public boolean isFailed() {
    return isFailed;
  }

  /**
   * Specifies whether this feature has ended unsuccessfully
   * @param isFailed true, if the feature is failed
   */
  public void setFailed(boolean isFailed) {
    this.isFailed = isFailed;
  }

}
