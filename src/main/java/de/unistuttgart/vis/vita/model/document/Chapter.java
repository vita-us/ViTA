package de.unistuttgart.vis.vita.model.document;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * Represents a chapter in a Document, including its id, number, title, text, length and range in
 * the Document.
 */
@Entity
public class Chapter {

  // attributes
  @GeneratedValue
  @Id
  private String id;
  private int number;
  private String title;
  private String text;
  private int length;
  
  @OneToOne
  private TextSpan range;
  
  @ManyToOne
  protected Document document;

  /**
   * Creates a new Chapter which belongs to the given Document.
   *
   * @param pDocument - the Document this Chapter belongs to
   */
  public Chapter(Document pDocument) {
    this.document = pDocument;
  }

  /**
   * @return the id of this Chapter
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id of the Chapter.
   *
   * @param newId - the id of this Chapter
   */
  public void setId(String newId) {
    this.id = newId;
  }

  /**
   * @return the number of this Chapter in the Document
   */
  public int getNumber() {
    return number;
  }

  /**
   * Sets the chapter number.
   *
   * @param newNumber - the number of this Chapter in the Document
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
   * @return the text of the chapter
   */
  public String getText() {
    return text;
  }

  /**
   * Sets the text of the Chapter.
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

}
