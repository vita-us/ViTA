package de.unistuttgart.vis.vita.model.document;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;

/**
 * The Occurence is defined by a sentence object and a range.
 */
public class Occurence extends AbstractEntityBase{

  @ManyToOne
  private Sentence sentence;
  
  @OneToOne
  private Range range;

  /**
   * Creates a new Occurence.
   */
  public Occurence() {
    // empty constructor for JPA
  }

  /**
   * Creates a new Occurence and sets all attributes to the given parameters.
   * 
   * @param sentence - the sentence of this Occurence.
   * @param range - the range of this Occurence.
   */
  public Occurence(Sentence sentence, Range range) {
    if(sentence == null){
      throw new IllegalArgumentException("sentence must not be null!");
    }
    if(range == null){
      throw new IllegalArgumentException("range must not be null!");
    }
    
    this.sentence = sentence;
    this.range = range;
  }

  /**
   * Get the Sentence of this Occurence.
   * 
   * @return the Sentence of this Occurence.
   */
  public Sentence getSentence() {
    return sentence;
  }

  /**
   * Set the Sentence of this Occurence.
   * 
   * @param sentence - the Sentence of this Occurence.
   */
  public void setSentence(Sentence sentence) {
    if(sentence == null){
      throw new IllegalArgumentException("sentence must not be null!");
    }
    
    this.sentence = sentence;
  }

  /**
   * Get the Range of this Occurence.
   * 
   * @return the Range of this Occurence.
   */
  public Range getRange() {
    return range;
  }

  /**
   * Set the Range of this Occurence.
   * 
   * @param range - the Range of this Occurence.
   */
  public void setRange(Range range) {
    if(range == null){
      throw new IllegalArgumentException("range must not be null!");
    }
    
    this.range = range;
  }
}
