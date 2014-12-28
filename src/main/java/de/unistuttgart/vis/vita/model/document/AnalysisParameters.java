package de.unistuttgart.vis.vita.model.document;

import javax.persistence.Entity;

import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;

@Entity
public class AnalysisParameters extends AbstractEntityBase {
  private int relationTimeStepCount = 20;

  public int getRelationTimeStepCount() {
    return relationTimeStepCount;
  }

  public void setRelationTimeStepCount(int relationTimeStepCount) {
    this.relationTimeStepCount = relationTimeStepCount;
  }
}
