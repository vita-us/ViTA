package de.unistuttgart.vis.vita.model.document;

import javax.persistence.Entity;

import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;

@Entity
public class AnalysisParameters extends AbstractEntityBase {
  private int relationTimeStepCount = 20;
  private int relationDistanceCount = 50;
  private int wordCloudItemsCount = 100;

  public int getRelationTimeStepCount() {
    return relationTimeStepCount;
  }

  public void setRelationTimeStepCount(int relationTimeStepCount) {
    this.relationTimeStepCount = relationTimeStepCount;
  }

  public int getRelationDistanceCount() {
    return relationDistanceCount;
  }

  public void setRelationDistanceCount(int relationDistanceCount) {
    this.relationDistanceCount = relationDistanceCount;
  }

  public int getWordCloudItemsCount() {
    return wordCloudItemsCount;
  }

  public void setWordCloudItemsCount(int wordCloudItemsCount) {
    this.wordCloudItemsCount = wordCloudItemsCount;
  }
      
}
