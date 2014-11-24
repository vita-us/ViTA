package de.unistuttgart.vis.vita.model.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A flat representation of {@link EntityRelation} only holding the id of the related entity and 
 * the weight of the relation.
 */
@XmlRootElement
public class FlatEntityRelation {
  
  @XmlElement(name = "relatedEntity")
  private String relatedEntityId;
  @XmlElement
  private double weight;
  
  public FlatEntityRelation() {
    // zero argument constructor needed
  }
  
  /**
   * Creates a new FlatEntityRelation holding the id of the related entity and the weight of the 
   * relation.
   * 
   * @param relEntityId
   * @param relWeight
   */
  public FlatEntityRelation(String relEntityId, double relWeight) {
    relatedEntityId = relEntityId;
    weight = relWeight;
  }

  /**
   * @return the id of the related entity
   */
  public String getRelatedEntityId() {
    return relatedEntityId;
  }

  /**
   * Sets the id of the related entity
   * 
   * @param relEntityId - the id of the related entity
   */
  public void setRelatedEntityId(String relEntityId) {
    this.relatedEntityId = relEntityId;
  }

  /**
   * @return the weight of the relation
   */
  public double getWeight() {
    return weight;
  }

  /**
   * Sets the weight of the relation.
   * 
   * @param relWeight - the weight of the relation
   */
  public void setWeight(double relWeight) {
    this.weight = relWeight;
  }

}
