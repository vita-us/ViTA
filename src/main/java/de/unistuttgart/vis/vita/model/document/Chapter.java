package de.unistuttgart.vis.vita.model.document;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;

import de.unistuttgart.vis.vita.model.TextRepository;
import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;
import de.unistuttgart.vis.vita.services.responses.occurrence.FlatOccurrence;

/**
 * Represents a chapter in a Document. It can hold its text content but does not persist it.
 */
@Entity
@Table(indexes = {@Index(columnList = "number")})
@NamedQueries({
    @NamedQuery(name = "Chapter.findAllChapters", query = "SELECT c " + "FROM Chapter c"),

    @NamedQuery(name = "Chapter.findChapterById", query = "SELECT c " + "FROM Chapter c "
        + "WHERE c.id = :chapterId"),

    @NamedQuery(name = "Chapter.findChapterByTitle", query = "SELECT c " + "FROM Chapter c "
        + "WHERE c.title = :chapterTitle"),

    @NamedQuery(name = "Chapter.findChapterByOffset", query = "SELECT c "
        + "FROM Document d, DocumentPart dp, Chapter c " + "WHERE d.id = :documentId "
        + "AND dp MEMBER OF d.content.parts " + "AND c MEMBER OF dp.chapters "
        + "AND :offset BETWEEN c.range.start.offset AND c.range.end.offset")})
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

  @OneToOne(cascade = CascadeType.ALL)
  private Range range;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "chapter")
  private List<Sentence> sentences;

  /**
   * Creates a new Chapter, setting all fields to default values.
   */
  public Chapter() {
    range =
        new Range(TextPosition.fromGlobalOffset(this, 0, 1), TextPosition.fromGlobalOffset(this, 0,
            1));
    sentences = new ArrayList<Sentence>();
  }

  /**
   * Gets some parts and concatenates their chapters and returns them as a list.
   * 
   * @param parts - the parts with the chapters. 
   * @return the chapters from the parts, in the same order as the given parts.
   */
  public static List<Chapter> transformPartsToChapters(List<DocumentPart> parts){
    List<Chapter> chapters = new ArrayList<Chapter>();
    for(DocumentPart part : parts){
      chapters.addAll(part.getChapters());
    }
    return chapters;
  }
  
  /**
   * Get a list of all sentences in this chapter. The sentences are sorted, so the n-th sentence in
   * the chapter is the n-th sentence in the list. The sentences don't know their text, only the
   * position of the text.
   * 
   * @return all sentences of this chapter.
   */
  public List<Sentence> getSentences() {
    return sentences;
  }

  /**
   * Set the list of sentences. The sentences are sorted, so the n-th sentence in the chapter is the
   * n-th sentence in the list.
   * 
   * @param sentences - all sentences of this chapter.
   */
  public void setSentences(List<Sentence> sentences) {
    if (sentences == null) {
      throw new IllegalArgumentException("sentences must not be null!");
    }
    this.sentences = sentences;
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
  public Range getRange() {
    return range;
  }

  /**
   * Sets the range of this Chapter in the Document.
   *
   * @param range - the range of this Chapter in the whole Document
   */
  public void setRange(Range range) {
    this.range = range;
  }

  /**
   * Check if the given range lies inside of the Chapter. A range lies inside of the Chapter, if its
   * start is behind the Chapters start or before the chapters end.
   * 
   * @param range - the range for which you want to know whether it is located inside of this
   *        Chapter or not.
   * @param useStartPosition - the start position (true) or the end position (false) of the range
   *        decides in which chapter the range is located
   * @return true: range is located inside of the chapter; false: range is not located inside of the
   *         chapter.
   */
  public boolean containsRange(Range range, boolean useStartPosition) {
    TextPosition rangePosition;
    if(useStartPosition){
      rangePosition = this.range.getStart();
    }else{
      rangePosition = this.range.getEnd();
    }
    return (this.range.getStart().compareTo(rangePosition) <= 0)
        && (this.range.getEnd().compareTo(rangePosition) >= 0);
  }

  /**
   * Gets the range of this chapter, as an {@link FlatOccurrence} object that contains the progress
   * property. Only exists in persisted objects.
   */
  @XmlElement(name = "range")
  public FlatOccurrence getRangeAsOccurrence() {
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
