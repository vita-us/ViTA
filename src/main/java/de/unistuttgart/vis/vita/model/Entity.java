package de.unistuttgart.vis.vita.model;

import java.util.Set;

import de.unistuttgart.vis.vita.model.text.TextSpan;

/**
 * Represents an entity found in the document including its id, type, displayed name, attributes,
 * ranking value, occurrences, fingerprint and relations to other entities.
 * 
 * @author Marc Weise
 * @version 0.1 29.07.2014
 */
public class Entity {

  private static final int MIN_RANK_VALUE = 1;
  private int id;
  private EntityType type;
  private String displayName;
  private Set<Attribute> attributes;
  private int rankingValue;
  private Set<TextSpan> occurrences;
  private boolean[] fingerprint;
  private Set<EntityRelation<Entity>> entityRelations;
  
  public Entity() {
    
  }

  /**
   * Creates a new instance of Entity.
   */
  public Entity(int id) {
    this.id = id;
  }

  /**
   * @return the id of the entity
   */
  public int getId() {
    return id;
  }

  /**
   * Sets a new Id for the entity.
   * 
   * @param newId
   */
  public void setId(int newId) {
    this.id = newId;
  }

  /**
   * @return the type of the entity
   */
  public EntityType getType() {
    return type;
  }

  public void setType(EntityType newType) {
    this.type = newType;
  }

  /**
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
   * @return a Set of all attributes of this entity
   */
  public Set<Attribute> getAttributes() {
    return attributes;
  }

  /**
   * Sets the attributes for this entity.
   * 
   * @param newAttributes - the new attributes for this entity
   */
  public void setAttributes(Set<Attribute> newAttributes) {
    this.attributes = newAttributes;
  }

  /**
   * @return the ranking value of the entity, where 1 is the highest rank
   */
  public int getRankingValue() {
    return rankingValue;
  }

  /**
   * Sets a new ranking value for this entity.
   * 
   * @param newRankingValue - the new ranking value must be 1 or greater
   */
  public void setRankingValue(int newRankingValue) {
    if (newRankingValue < MIN_RANK_VALUE) {
      throw new IllegalArgumentException("the ranking value must be " + MIN_RANK_VALUE
          + " or greater!");
    }
    this.rankingValue = newRankingValue;
  }

  /**
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
   * @return the fingerprint vector
   */
  public boolean[] getFingerprint() {
    return fingerprint;
  }

  /**
   * Sets the fingerprint vector for this entity
   * 
   * @param newFingerprint - the new fingerprint vector for this entity
   */
  public void setFingerprint(boolean[] newFingerprint) {
    this.fingerprint = newFingerprint;
  }

  /**
   * @return a Set of all relations to other entities
   */
  public Set<EntityRelation<Entity>> getEntityRelations() {
    return entityRelations;
  }

  /**
   * Sets the relations for this entity.
   * 
   * @param newEntityRelations - a set of new relations for this entity
   */
  public void setEntityRelations(Set<EntityRelation<Entity>> newEntityRelations) {
    this.entityRelations = newEntityRelations;
  }

}
