package de.unistuttgart.vis.vita.model.document;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;

/**
 * The Occurrence is defined by a sentence and a range. The range specifies the place or person to
 * which the Occurrence belongs.
 */
@Entity
@NamedQueries({
  @NamedQuery(name = "Range.findAllTextSpans", query = "SELECT ts " + "FROM Range ts"),

  // for returning the exact spans for an entity in a given range
  @NamedQuery(name = "Range.findTextSpansForEntity", query = "SELECT ts "
      + "FROM Range ts, Entity e " + "WHERE e.id = :entityId "
      + "AND ts MEMBER OF e.occurrences "
      // range checks
      + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
      + "AND ts.end.offset BETWEEN :rangeStart AND :rangeEnd "
      // right ordering
      + "ORDER BY ts.start.offset"),

  // for checking the amount of spans for an entity in a given range
  @NamedQuery(name = "Range.getNumberOfTextSpansForEntity", query = "SELECT COUNT(ts) "
      + "FROM Range ts, Entity e " + "WHERE e.id = :entityId "
      + "AND ts MEMBER OF e.occurrences "
      // range checks
      + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
      + "AND ts.end.offset BETWEEN :rangeStart AND :rangeEnd"),

  // for returning the exact spans for an attribute in a given range
  @NamedQuery(name = "Range.findTextSpansForAttribute", query = "SELECT ts "
      + "FROM Range ts, Entity e, Attribute a " + "WHERE e.id = :entityId "
      + "AND a MEMBER OF e.attributes " + "AND a.id = :attributeId "
      + "AND ts MEMBER OF a.occurrences "
      // range checks
      + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
      + "AND ts.end.offset BETWEEN :rangeStart AND :rangeEnd "
      // right ordering
      + "ORDER BY ts.start.offset"),

  // for checking the amount of spans for an attribute in a given range
  @NamedQuery(name = "Range.getNumberOfTextSpansForAttribute", query = "SELECT COUNT(ts) "
      + "FROM Range ts, Entity e, Attribute a " + "WHERE e.id = :entityId "
      + "AND a MEMBER OF e.attributes " + "AND a.id = :attributeId "
      + "AND ts MEMBER OF a.occurrences "
      // range checks
      + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
      + "AND ts.end.offset BETWEEN :rangeStart AND :rangeEnd"),

  // gets the occurrences of all entities
  @NamedQuery(name = "Range.findTextSpansForEntities", query = "SELECT ts "
      + "FROM Range ts, Entity e " + "WHERE e.id IN :entityIds "
      + "AND ts MEMBER OF e.occurrences "
      // range checks
      + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
      + "AND ts.end.offset BETWEEN :rangeStart AND :rangeEnd "
      // Null checks
      + "AND ts.start.chapter IS NOT NULL "
      // right ordering
      + "ORDER BY ts.start.offset"),

  // checks whether a set of entities occur in a range (for relation occurrences)
  @NamedQuery(name = "Range.getNumberOfOccurringEntities", query = "SELECT COUNT(DISTINCT e.id) "
      + "FROM Entity e " + "INNER JOIN e.occurrences ts " + "WHERE e.id IN :entityIds "
      // range checks
      + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
      + "AND ts.start.offset BETWEEN :rangeStart AND :rangeEnd "
      // Null checks
      + "AND ts.start.chapter IS NOT NULL " + "AND ts.start.chapter IS NOT NULL"),

  @NamedQuery(name = "Range.findTextSpanById", query = "SELECT ts " + "FROM Range ts "
      + "WHERE ts.id = :RangeId")})
public class Occurrence extends AbstractEntityBase {

  @ManyToOne
  private Sentence sentence;

  @OneToOne
  private Range range;

  /**
   * Creates a new Occurrence.
   */
  public Occurrence() {
    // empty constructor for JPA
  }

  /**
   * Creates a new Occurrence and sets all attributes to the given parameters.
   * 
   * @param sentence - the sentence of this Occurrence.
   * @param range - the range of this Occurrence.
   */
  public Occurrence(Sentence sentence, Range range) {
    if (sentence == null) {
      throw new IllegalArgumentException("sentence must not be null!");
    }
    if (range == null) {
      throw new IllegalArgumentException("range must not be null!");
    }

    this.sentence = sentence;
    this.range = range;
  }

  /**
   * Get the Sentence of this Occurrence.
   * 
   * @return the Sentence of this Occurrence.
   */
  public Sentence getSentence() {
    return sentence;
  }

  /**
   * Set the Sentence of this Occurrence.
   * 
   * @param sentence - the Sentence of this Occurrence.
   */
  public void setSentence(Sentence sentence) {
    if (sentence == null) {
      throw new IllegalArgumentException("sentence must not be null!");
    }

    this.sentence = sentence;
  }

  /**
   * Get the Range of this Occurrence.
   * 
   * @return the Range of this Occurrence.
   */
  public Range getRange() {
    return range;
  }

  /**
   * Set the Range of this Occurrence.
   * 
   * @param range - the Range of this Occurrence.
   */
  public void setRange(Range range) {
    if (range == null) {
      throw new IllegalArgumentException("range must not be null!");
    }

    this.range = range;
  }
}
