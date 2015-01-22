package de.unistuttgart.vis.vita.model.document;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;

/**
 * Represents a sentence from the document. The sentence knows the chapter it is located in, its
 * range in the book and its unique index in the document.
 */
@Entity
@XmlRootElement
public class Sentence extends AbstractEntityBase {

  @Embedded
  private Range range;

  @ManyToOne
  private Chapter chapter;

  @Column(name = "sentenceIndex")
  private int index;

  /**
   * Creates a new Sentence.
   */
  public Sentence() {
    range = new Range();
    chapter = new Chapter();
  }

  /**
   * Creates a new Sentence with the given parameters.
   * 
   * @param range - the range of the sentence in the document.
   * @param chapter - the chapter in which the sentence is located.
   * @param index - the unique index of the sentence in the document.
   */
  public Sentence(Range range, Chapter chapter, int index) {
    if (range == null) {
      throw new IllegalArgumentException("range must not be null!");
    }
    if (chapter == null) {
      throw new IllegalArgumentException("chapter must not be null!");
    }
    if (index < 0) {
      throw new IllegalArgumentException("index must not be negative!");
    }

    this.range = range;
    this.chapter = chapter;
    this.index = index;
  }

  /**
   * Get the range of the Sentence in the document.
   * 
   * @return the range of the Sentence in the document.
   */
  public Range getRange() {
    return range;
  }

  /**
   * Set the range of the Sentence in the document.
   * 
   * @param range - the range of the Sentence in the document.
   */
  public void setRange(Range range) {
    if (range == null) {
      throw new IllegalArgumentException("range must not be null!");
    }

    this.range = range;
  }

  /**
   * Get the chapter in which the Sentence is located.
   * 
   * @return the chapter in which the Sentence is located.
   */
  public Chapter getChapter() {
    return chapter;
  }

  /**
   * Set the chapter in which the Sentence is located.
   * 
   * @param chapter - the chapter in which the Sentence is located.
   */
  public void setChapter(Chapter chapter) {
    if (chapter == null) {
      throw new IllegalArgumentException("chapter must not be null!");
    }

    this.chapter = chapter;
  }

  /**
   * Get the unique index of the sentence in the document. The first index is 0 and they are ordered
   * by the index. If there exist n sentences in the document, the last one is n-1.
   * 
   * @return the unique index of the sentence.
   */
  public int getIndex() {
    return index;
  }

  /**
   * Set the unique index of the sentence in the document. The first index is 0 and they are ordered
   * by the index. If there exist n sentences in the document, the last one is n-1.
   * 
   * @param index - the unique index of the sentence.
   */
  public void setIndex(int index) {
    if (index < 0) {
      throw new IllegalArgumentException("Index must not be negative!");
    }

    this.index = index;
  }

}
