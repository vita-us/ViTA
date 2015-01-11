package de.unistuttgart.vis.vita.model.document;

import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;

import javax.persistence.Entity;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
public class AnalysisParameters extends AbstractEntityBase {
  @XmlElement
  @Min(1)
  @Max(1000)
  private int relationTimeStepCount = 20;

  @XmlElement
  @Min(10)
  @Max(100)
  private int wordCloudItemsCount = 100;

  @XmlElement
  private boolean stopWordListEnabled = true;

  public int getRelationTimeStepCount() {
    return relationTimeStepCount;
  }

  public void setRelationTimeStepCount(int relationTimeStepCount) {
    this.relationTimeStepCount = relationTimeStepCount;
  }

  public int getWordCloudItemsCount() {
    return wordCloudItemsCount;
  }

  public void setWordCloudItemsCount(int wordCloudItemsCount) {
    this.wordCloudItemsCount = wordCloudItemsCount;
  }

  public boolean isStopWordListEnabled() {
    return stopWordListEnabled;
  }

  public void setStopWordListEnabled(boolean stopWordListEnabled) {
    this.stopWordListEnabled = stopWordListEnabled;
  }
}
