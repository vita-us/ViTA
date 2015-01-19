package de.unistuttgart.vis.vita.model.document;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;
@Entity
public class Occurrence extends AbstractEntityBase {

  @ManyToOne(cascade = CascadeType.ALL)
  private Sentence sentence;

  @OneToOne(cascade = CascadeType.ALL)
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
