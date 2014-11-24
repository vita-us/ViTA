package de.unistuttgart.vis.vita.analysis;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;


/**
 * The phases an analysis goes through. Context can be a single module or a whole document.
 */
@XmlEnum
public enum AnalysisStatus {
  /**
   * Indicates that the analysis can be started
   */
  @XmlEnumValue("scheduled")
  READY,

  /**
   * Indicates that the analysis has been started
   */
  @XmlEnumValue("running")
  RUNNING,

  /**
   * Indicates that the analysis has been cancelled by the owner (not by a module failure)
   */
  @XmlEnumValue("cancelled")
  CANCELLED,

  /**
   * Indicates that the analysis has been stopped and did not succeed
   */
  @XmlEnumValue("failed")
  FAILED,

  /**
   * Indicates that the analysis has been stopped and suceeded
   */
  @XmlEnumValue("success")
  FINISHED;

  @Override
  public String toString() {
    return name().toLowerCase();
  }
}
