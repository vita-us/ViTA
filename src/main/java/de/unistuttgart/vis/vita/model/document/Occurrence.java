package de.unistuttgart.vis.vita.model.document;


import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.apache.commons.lang.builder.HashCodeBuilder;

import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;

/**
 * Defines the bounds of a text block (the Place, Person,... the Occurrence belongs to) as Range and
 * the sentence this block is located in. Is not aware of the actual text within the bounds.
 */
@Entity
public class Occurrence extends AbstractEntityBase implements Comparable<Occurrence> {

  @ManyToOne(cascade = CascadeType.ALL)
  private Sentence sentence;

  @Embedded
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

  @Override
  public int compareTo(Occurrence otherOccurrence) {
    return range.compareTo(otherOccurrence.getRange());
  }

  /**
   * Indicates if obj is a Occurrence representing the same range (the person, place, ...) as this
   * text span.
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Occurrence)) {
      return false;
    }

    Occurrence other = (Occurrence) obj;
    return range.equals(other.getRange());
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(range.getStart()).append(range.getEnd()).hashCode();
  }

  @Override
  public String toString() {
    return String.format("Occurrence %s ... %s", range.getStart(), range.getEnd());
  }

}
