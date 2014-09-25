package de.unistuttgart.vis.vita.model.document;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import de.unistuttgart.vis.vita.model.TextRepository;
import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;

/**
 * Represents a chapter in a Document. It can hold its text content but does not persist it.
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Chapter.findAllChapters", query = "SELECT c " + "FROM Chapter c"),

    @NamedQuery(name = "Chapter.findChapterById", query = "SELECT c " + "FROM Chapter c "
        + "WHERE c.id = :chapterId"),

    @NamedQuery(name = "Chapter.findChapterByTitle", query = "SELECT c " + "FROM Chapter c "
        + "WHERE c.title = :chapterTitle")})
public class Chapter extends AbstractEntityBase {
  
  private int number;
  private String title;
  private int length;
  
  // text attribute is transient, this means it will not be persisted in the database!
  @Transient
  private String text;

  @OneToOne
  private TextSpan range;

  /**
   * Creates a new Chapter, setting all fields to default values.
   */
  public Chapter() {
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
   * @param the number, starting from 1
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

}
