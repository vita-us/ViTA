package de.unistuttgart.vis.vita.model.document;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;

import de.unistuttgart.vis.vita.model.TextRepository;
import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;
import de.unistuttgart.vis.vita.services.responses.occurrence.Occurrence;

/**
 * Represents a chapter in a Document. It can hold its text content but does not persist it.
 */
@Entity
@Table(indexes={
  @Index(columnList="number")
})
public class Chapter extends AbstractEntityBase {

  private int number;

  @Column(length = 1000)
  private String title;

  private int length;

  // text attribute is transient, this means it will not be persisted in the database!
  @Transient
  private String text;

  // is required for getOccurrence()
  private int documentLength;

  @OneToOne(cascade=CascadeType.ALL)
  private TextSpan range;

  /**
   * Creates a new Chapter, setting all fields to default values.
   */
  public Chapter() {
    range = new TextSpan(TextPosition.fromGlobalOffset(this, 0),
        TextPosition.fromGlobalOffset(this, 0));
  }

  /**
   * Gets the readable number of this chapter in the context of the document part it belongs to
   *
   * @return the number, starting from 1
   */
  public int getNumber() {
    return number;
  }

  /**
   * Sets the readable number of this chapter in the context of the document part it belongs to
   *
   * @param newNumber - the number starting from 1
   */
  public void setNumber(int newNumber) {
    this.number = newNumber;
  }

  /**
   * @return the title of the Chapter
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title of the Chapter.
   *
   * @param title - the title of this Chapter
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return the text of the chapter, or {@code null} if it is not yet populated
   */
  public String getText() {
    return text;
  }

  /**
   * Sets the text of the Chapter.
   *
   * This property will not be persisted with JPA. It should be stored with {@link TextRepository}
   * instead.
   *
   * @param text - the text content of this Chapter
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * @return the length of this Chapter as number of characters
   */
  public int getLength() {
    return length;
  }

  /**
   * Sets the length of this Chapter. This is equal to the number of characters in the text of this
   * Chapter.
   *
   * @param length - the length of this Chapter
   */
  public void setLength(int length) {
    this.length = length;
  }

  /**
   * @return the range of this Chapter in the whole Document
   */
  public TextSpan getRange() {
    return range;
  }

  /**
   * Sets the range of this Chapter in the Document.
   *
   * @param range - the range of this Chapter in the whole Document
   */
  public void setRange(TextSpan range) {
    this.range = range;
  }

  /**
   * Gets the range of this chapter, as an {@link Occurrence} object that contains the progress
   * property. Only exists in persisted objects.
   */
  @XmlElement(name = "range")
  public Occurrence getRangeAsOccurrence() {
    return range.toOccurrence(documentLength);
  }

  /**
   *
   * @param length
   */
  public void setDocumentLength(int length) {
    this.documentLength = length;
  }
}
