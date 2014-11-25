package de.unistuttgart.vis.vita.model.document;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Represents the meta data of a Document, including information like title, author, publisher, year
 * of publication, genre and edition of the Document.
 */
@Embeddable
public class DocumentMetadata {
  
  // attributes
  @Column(length = 1000)
  private String title;
  private String author;
  private String publisher;
  private int publishYear;
  private String genre;
  private String edition;
  
  /**
   * Creates a new instance of DocumentMetadata, setting all attributes to default values.
   */
  public DocumentMetadata() {
    // do nothing
  }
  /**
   * Creates a new instance of DocumentMetadata, setting title and author to the given values.
   * 
   * @param pTitle - the title of the Document this metadata refer to
   * @param pAuthor - the author of the Document this metadata refer to
   */
  public DocumentMetadata(String pTitle, String pAuthor) {
    this.title = pTitle;
    this.author = pAuthor;
  }
  
  /**
   * @return the title under which the Document is published
   */
  public String getTitle() {
    return title;
  }
  
  /**
   * Sets the title for the Document.
   *
   * @param newTitle The new title of the document.
   */
  public void setTitle(String newTitle) {
    this.title = newTitle;
  }
  
  /**
   * @return the author of the Document
   */
  public String getAuthor() {
    return author;
  }
  
  /**
   * Set the author of the Document.
   * 
   * @param newAuthor - the author of the Document
   */
  public void setAuthor(String newAuthor) {
    this.author = newAuthor;
  }
  
  /**
   * @return the name of the publisher of this Document
   */
  public String getPublisher() {
    return publisher;
  }
  
  /**
   * Sets the name of the publisher of this Document.
   * 
   * @param newPublisher - the name of the publisher
   */
  public void setPublisher(String newPublisher) {
    this.publisher = newPublisher;
  }
  
  /**
   * @return year when the Document was published
   */
  public int getPublishYear() {
    return publishYear;
  }
  
  /**
   * Sets the year of publication.
   * 
   * @param newPublishYear - the year when the document was published
   */
  public void setPublishYear(int newPublishYear) {
    this.publishYear = newPublishYear;
  }
  
  /**
   * @return the name of the genre
   */
  public String getGenre() {
    return genre;
  }
  
  /**
   * Sets the genre for the Document.
   * 
   * @param newGenre - the genre of the Document
   */
  public void setGenre(String newGenre) {
    this.genre = newGenre;
  }
  
  /**
   * @return the edition name of the Document
   */
  public String getEdition() {
    return edition;
  }
  
  /**
   * Sets the edition name of the document.
   * 
   * @param edition - the name of the edition of this Document
   */
  public void setEdition(String edition) {
    this.edition = edition;
  }
  
}
