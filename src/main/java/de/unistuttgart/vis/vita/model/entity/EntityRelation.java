package de.unistuttgart.vis.vita.model.entity;

import java.util.Arrays;

import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.hibernate.annotations.Target;

/**
 * Represents a Relation between two Entities.
 *
 * @param <Entity> - the type of the other entity
 */
@javax.persistence.Entity
@NamedQueries({
    @NamedQuery(name = "EntityRelation.findAllEntityRelations", 
                query = "SELECT er "
                      + "FROM EntityRelation er"),
                      
    @NamedQuery(name = "EntityRelation.findRelationsForEntities",
                query = "SELECT er "
                      + "FROM Entity e JOIN e.entityRelations er "
                      + "WHERE e.id IN :entityIds "),
        
    @NamedQuery(name = "EntityRelation.findRelationsForEntitiesAndType",
                query = "SELECT er "
                      + "FROM Entity e JOIN e.entityRelations er "
                      + "WHERE e.id IN :entityIds "
                      + "AND er.relatedEntity.id IN :entityIds "
                      + "AND er.relatedEntity.class = :type"),

    @NamedQuery(name = "EntityRelation.findEntityRelationById", 
                query = "SELECT er "
                      + "FROM EntityRelation er " 
                      + "WHERE er.id = :entityRelationId")})
public class EntityRelation extends AbstractEntityBase {

  // constants
  private static final int WEIGHT_MIN = 0;
  private static final int WEIGHT_MAX = 1;

  private double weight;
  
  private double[] weightOverTime;
  
  @ManyToOne
  @JoinTable(name="OriginId")
  private Entity originEntity;
  
  // only entity relations will be persisted
  @Target(Entity.class)
  @ManyToOne
  @JoinTable(name="TargetId")
  private Entity relatedEntity;

  public Entity getOriginEntity() {
    return originEntity;
  }

  public void setOriginEntity(Entity originEntity) {
    this.originEntity = originEntity;
  }

  /**
   * @return entity which is target of this relation
   */
  public Entity getRelatedEntity() {
    return relatedEntity;
  }

  /**
   * Sets the entity which is target of this relation.
   *
   * @param relatedEntity - the related entity
   */
  public void setRelatedEntity(Entity relatedEntity) {
    this.relatedEntity = relatedEntity;
  }

  /**
   * Returns how strong this relation is, returning a value between 0 (very weak) and 1 (very
   * strong).
   *
   * @return the weight of this relation
   */
  public double getWeight() {
    return weight;
  }

  /**
   * Sets the weight of this relation, indicating how strong it is.
   *
   * @param weight - value from 0.0 (very weak relation) to 1.0 (very strong relation)
   */
  public void setWeight(double weight) {
    if (weight < WEIGHT_MIN || weight > WEIGHT_MAX) {
      throw new IllegalArgumentException("weight of relation must be between 0 and 1!");
    }
    this.weight = weight;
  }
  
  /**
   * Gets the weight of this relation when only a part of the document is considered
   * 
   * @param start the start position as a value between 0 and 1, where 0 is the start of the
   *        document and 1 is the end
   * @param end the end position as a value between 0 and 1, where 0 is the start of the
   *        document and 1 is the end
   * @return the weight, normalized to 0..1
   */
  public double getWeightForRange(double start, double end) {
    if (start < 0.001 && end > 0.999) {
      return weight;
    }
    
    if (weightOverTime.length == 0) {
      return 0;
    }
    
    if (weightOverTime.length == 1) {
      // would mess up the first/lastIndexFactor
      return weightOverTime[0];
    }
    
    if (end - start < 0.0001) {
      // Avoid division by zero
      return 0;
    }
    
    double startInSteps = start * weightOverTime.length;
    double endInSteps = end * weightOverTime.length;
    
    // These are the steps that are used completely
    int startIndex = (int)Math.ceil(startInSteps);
    int endIndex = (int)Math.floor(endInSteps);
    
    // These are the incomplete steps
    double firstIndexFactor = startInSteps - (startIndex - 1);
    double lastIndexFactor = endInSteps - endIndex;
    
    double value = 0;
    if (startIndex > 0) {
      value += weightOverTime[startIndex - 1] * firstIndexFactor;
    }
    if (endIndex < weightOverTime.length - 1) {
      value += weightOverTime[endIndex] * lastIndexFactor;
    }
    
    for (int i = startIndex; i < endIndex; i++) {
      value += weightOverTime[i];
    }
    
    return value / (end - start) / weightOverTime.length;
  }
  
  public void setWeightOverTime(double[] steps) {
    this.weightOverTime = Arrays.copyOf(steps, steps.length);
  }
}
