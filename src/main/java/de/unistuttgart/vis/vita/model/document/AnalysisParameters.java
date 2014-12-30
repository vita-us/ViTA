package de.unistuttgart.vis.vita.model.document;

import javax.persistence.Entity;

import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;

@Entity
public class AnalysisParameters extends AbstractEntityBase {
  private int relationTimeStepCount = 20;
  private int wordCloudItemsCount = 100;
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

  public void setWordCloudItemsCount(int wordCloudCount) {
    this.wordCloudItemsCount = wordCloudCount;
  }

  public boolean isStopWordListEnabled() {
    return stopWordListEnabled;
  }

  public void setStopWordListEnabled(boolean stopWordListEnabled) {
    this.stopWordListEnabled = stopWordListEnabled;
  } 
  
}
