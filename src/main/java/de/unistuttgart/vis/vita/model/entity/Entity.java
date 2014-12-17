package de.unistuttgart.vis.vita.model.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.model.wordcloud.WordCloud;

/**
 * Represents an entity found in the document including its id, type, displayed name, attributes,
 * ranking value, occurrences, fingerprint and relations to other entities.
 */
@javax.persistence.Entity
@Table(indexes={
    @Index(columnList="rankingValue")
  })
@NamedQueries(
  @NamedQuery(name = "Entity.findEntityById",
          query = "SELECT e "
                + "FROM Entity e "
                + "WHERE e.id = :entityId")
)
@Inheritance
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
@XmlRootElement
@XmlSeeAlso({Person.class, Place.class})
public abstract class Entity extends AbstractEntityBase {

  private static final int MIN_RANK_VALUE = 1;

  @Column(length = 1000)
  private String displayName;
  private boolean[] fingerprint;
  private int rankingValue;
  private int frequency;

  @OneToMany(cascade = CascadeType.ALL)
  @XmlElement(required = true)
  private Set<Attribute> attributes;

  @OneToMany(cascade = CascadeType.ALL)
  @OrderBy("start.offset ASC")
  private SortedSet<TextSpan> occurrences;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "originEntity")
  @XmlElement(name = "entityRelations")
  @XmlJavaTypeAdapter(FlatEntityRelationAdapter.class)
  private Set<EntityRelation> entityRelations;

  @OneToOne
  private WordCloud wordCloud;

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
   * Sets the a new frequency value for the entity
   *
   * @param newFrequency - count of occurrences of this entity
   */
  public void setFrequency(int newFrequency) {
    this.frequency = newFrequency;
  }
  
  /**
   * @return the frequency value of the entity
   */
  public int getFrequency() {
    return frequency;
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

  public WordCloud getWordCloud() {
    return wordCloud;
  }

  public void setWordCloud(WordCloud wordCloud) {
    this.wordCloud = wordCloud;
  }

}
