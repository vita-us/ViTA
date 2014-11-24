package de.unistuttgart.vis.vita.model.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Inheritance;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import de.unistuttgart.vis.vita.model.document.TextSpan;

/**
 * Represents an entity found in the document including its id, type, displayed name, attributes,
 * ranking value, occurrences, fingerprint and relations to other entities.
 */
@javax.persistence.Entity
@Inheritance
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class Entity extends AbstractEntityBase {

  // constants
  private static final int MIN_RANK_VALUE = 1;

  private String displayName;
  private boolean[] fingerprint;
  private int rankingValue;

  @OneToMany(cascade = CascadeType.ALL)
  private Set<Attribute> attributes;

  @OneToMany(cascade = CascadeType.ALL)
  @OrderBy("start.offset ASC")
  private SortedSet<TextSpan> occurrences;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "originEntity")
  private Set<EntityRelation> entityRelations;

  /**
   * Creates a new entity with default values.
   */
  public Entity() {
    attributes = new HashSet<>();
    occurrences = new TreeSet<>();
    entityRelations = new HashSet<>();
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
  public SortedSet<TextSpan> getOccurrences() {
    return occurrences;
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
  public Set<EntityRelation> getEntityRelations() {
    return entityRelations;
  }

}
