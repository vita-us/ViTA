package de.unistuttgart.vis.vita.model.document;

import javax.persistence.Embeddable;

/**
 * Represents all metrics of a Document, including the number of characters, words, chapters,
 * persons and places.
 */
@Embeddable
public class DocumentMetrics {

  // attributes
  private int characterCount;
  private int wordCount;
  private int chapterCount;
  private int personCount;
  private int placeCount;

  /**
   * Creates a new instance of DocumentMetrics, setting all attributes to zero.
   */
  public DocumentMetrics() {
    // do nothing
  }

  /**
   * Creates a new instance of DocumentMetrics with the given number of characters.
   *
   * @param charCount - the number of characters in the whole document
   */
  public DocumentMetrics(int charCount) {
    this.characterCount = charCount;
  }

  /**
   * @return the total number of characters in the Document this metrics refer to
   */
  public int getCharacterCount() {
    return characterCount;
  }

  /**
   * Sets the number of characters in the Document.
   *
   * @param characterCount - the total number of characters in the Document this metrics refer to.
   */
  public void setCharacterCount(int characterCount) {
    this.characterCount = characterCount;
  }

  /**
   * @return the total number of words in the Document this metrics refer to
   */
  public int getWordCount() {
    return wordCount;
  }

  /**
   * Sets the number of words in the Document.
   *
   * @param wordCount - the total number of words in the Document this metrics refer to
   */
  public void setWordCount(int wordCount) {
    this.wordCount = wordCount;
  }

  /**
   * @return the total number of chapters in the Document this metrics refer to.
   */
  public int getChapterCount() {
    return chapterCount;
  }

  /**
   * Sets the number of chapters in the Document.
   *
   * @param chapterCount - the total number of chapters in the Document this metrics refer to
   */
  public void setChapterCount(int chapterCount) {
    this.chapterCount = chapterCount;
  }

  /**
   * @return the total number of persons mentioned in the Document this metrics refer to
   */
  public int getPersonCount() {
    return personCount;
  }

  /**
   * Sets the number of persons mentioned in the Document.
   *
   * @param personCount - the total number of persons appearing in the Document this metrics refer
   *                    to.
   */
  public void setPersonCount(int personCount) {
    this.personCount = personCount;
  }

  /**
   * @return the total number of places mentioned in the Document this metrics refer to.
   */
  public int getPlaceCount() {
    return placeCount;
  }

  /**
   * Sets the number of places mentioned in the Document.
   *
   * @param placeCount - the total number of places mentioned in the Document this metrics refer
   *                   to.
   */
  public void setPlaceCount(int placeCount) {
    this.placeCount = placeCount;
  }

}
