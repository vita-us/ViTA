package de.unistuttgart.vis.vita.model.entity;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.OneToMany;

import de.unistuttgart.vis.vita.model.document.TextSpan;

/**
 * Represents an entity found in the document including its id, type, displayed name, attributes,
 * ranking value, occurrences, fingerprint and relations to other entities.
 */
@javax.persistence.Entity
@Inheritance
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class Entity {

  // constants
  private static final int MIN_RANK_VALUE = 1;

  @GeneratedValue
  @Id
  private int id;

  private String displayName;
  private boolean[] fingerprint;
  private int rankingValue;

  @OneToMany
  private Set<Attribute> attributes;

  @OneToMany
  private Set<TextSpan> occurrences;

  @OneToMany
  private Set<EntityRelation<Entity>> entityRelations;

  /**
   * Creates a new entity with default values.
   */
  public Entity() {
    attributes = new TreeSet<>();
    occurrences = new TreeSet<>();
    entityRelations = new TreeSet<>();
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
   */
  public void setId(int newId) {
    this.id = newId;
  }

  /**
   * @return the type of the entity
   */
  public abstract EntityType getType();

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
   * Gets a bit vector that divides the whole document in spans of equal lengths and determines
   * whether this entity occurs in a given span (true) or not (false).
   * 
   * @return the fingerprint vector
   */
  public boolean[] getFingerprint() {
    return fingerprint;
  }

  /**
   * Sets a bit vector that divides the whole document in spans of equal lengths and determines
   * whether this entity occurs in a given span (true) or not (false).
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
