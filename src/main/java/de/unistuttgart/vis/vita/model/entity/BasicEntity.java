package de.unistuttgart.vis.vita.model.entity;

import java.util.Set;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import de.unistuttgart.vis.vita.model.document.TextSpan;

/**
 * The information about an entity that can be collected in the first pass
 */
@javax.persistence.Entity
public class BasicEntity {
  
  @GeneratedValue
  @Id
  private int id;
  
  private String displayName;
  private EntityType type;
  
  @OneToMany
  private Set<Attribute> nameAttributes;
  @OneToMany
  private Set<TextSpan> occurrences;
  @OneToMany
  private Set<EntityRelation<BasicEntity>> entityRelations;

  /**
   * @return the id of this BasicEntity
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the new id for this BasicEntity
   *
   * @param newId - the new id for this BasicEntity
   */
  public void setId(int newId) {
    this.id = newId;
  }

  /**
   * Gets the name under which this entity will be shown
   * 
   * @return the name under which this entity will be shown
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Sets the name under which this entity is shown in the graphical user interface.
   * 
   * @param newDisplayName - the new name under which this entity should be shown
   */
  public void setDisplayName(String newDisplayName) {
    this.displayName = newDisplayName;
  }

  /**
   * Gets all the names under which the entity is known
   * 
   * @return the names under which the entity is known
   */
  public Set<Attribute> getNameAttributes() {
    return nameAttributes;
  }

  /**
   * Sets the names under which the entity is known
   * 
   * @param nameAttributes the names under which the entity is known
   */
  public void setNameAttributes(Set<Attribute> nameAttributes) {
    this.nameAttributes = nameAttributes;
  }

  /**
   * Gets all occurrences of this entity in the document
   * 
   * @return Set of all occurrences of this entity in the document
   */
  public Set<TextSpan> getOccurences() {
    return occurrences;
  }

  /**
   * Sets the occurrences for this entity.
   * 
   * @param newOccurences - a set of new occurrences for this entity
   */
  public void setOccurences(Set<TextSpan> newOccurences) {
    this.occurrences = newOccurences;
  }

  /**
   * Indicates of which type this entity is
   * 
   * @return either PERSON or PLACE
   */
  public EntityType getType() {
    return type;
  }

  /**
   * Sets of which type this entity is
   * 
   * @param type either PERSON or PLACE
   */
  public void setType(EntityType type) {
    this.type = type;
  }

  /**
   * Gets relations with other entities
   * 
   * @return the set of relations
   */
  public Set<EntityRelation<BasicEntity>> getEntityRelations() {
    return entityRelations;
  }

  /**
   * Sets the relations with other entities
   * 
   * @param entityRelations the set of relations
   */
  public void setEntityRelations(Set<EntityRelation<BasicEntity>> entityRelations) {
    this.entityRelations = entityRelations;
  }
}
