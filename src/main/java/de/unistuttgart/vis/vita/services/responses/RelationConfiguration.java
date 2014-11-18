package de.unistuttgart.vis.vita.services.responses;

import javax.xml.bind.annotation.XmlRootElement;

import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.EntityRelation;

/**
 * Holds the configuration of an EntityRelation, including both ids and the weight of the relation.
 */
@XmlRootElement
public class RelationConfiguration {

  private String entityAId;
  private String entityBId;
  private double weight;
  
  /**
   * Creates a new instance of RelationConfiguration, setting all attributes to default values.
   */
  public RelationConfiguration() {
    // parameterless constructor needed
  }

  /**
   * Creates a new RelationConfiguration storing the minimal representation of the given 
   * EntityRelation.
   * 
   * @param rel - the EntityRelation this RelationConfiguration should represent
   */
  public RelationConfiguration(EntityRelation rel) {
    if (rel == null) {
      throw new IllegalArgumentException("EntityRelation must not be null!");
    }
    this.entityAId = rel.getOriginEntity().getId();
    this.entityBId = rel.getRelatedEntity().getId();
    this.weight = rel.getWeight();
  }

  /**
   * @return the id of the origin entity
   */
  public String getEntityAId() {
    return entityAId;
  }

  /**
   * Sets the id for the origin entity.
   * 
   * @param originId - the id of the origin entity
   */
  public void setEntityAId(String originId) {
    this.entityAId = originId;
  }

  /**
   * @return the id of the target entity
   */
  public String getEntityBId() {
    return entityBId;
  }

  /**
   * Sets the id for the origin entity.
   * 
   * @param targetId - the id of the target entity
   */
  public void setEntityBId(String targetId) {
    this.entityBId = targetId;
  }

  /**
   * @return the weight of the represented relation
   */
  public double getWeight() {
    return weight;
  }
  
  /**
   * Sets the weight of the represented relation.
   * 
   * @param relWeight - the weight of the represented relation
   */
  public void setWeight(double relWeight) {
    this.weight = relWeight;
  }

}
