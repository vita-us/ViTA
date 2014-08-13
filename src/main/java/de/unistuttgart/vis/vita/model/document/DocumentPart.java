package de.unistuttgart.vis.vita.model.document;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Represents a group of chapters, usually called "part" or "book" in a document.
 */
@Entity
public class DocumentPart {
  @Id
  @GeneratedValue
  private String id;

  private int number;
  private String title;

  @OneToMany
  private List<Chapter> chapters = new ArrayList<Chapter>();

  /**
   * Gets an identifier that is unique in the database
   */
  public String getId() {
    return id;
  }

  /**
   * Sets an identifier that is unique in the database
   *
   * @param newId the id
   */
  public void setId(String newId) {
    this.id = newId;
  }

  /**
   * Gets the readable number of this part in the context of the document
   * 
   * @return the number, starting from 1
   */
  public int getNumber() {
    return number;
  }

  /**
   * Sets the readable number of this part in the context of the document
   *
   * @param the number, starting from 1
   */
  public void setNumber(int newNumber) {
    this.number = newNumber;
  }

  /**
   * @return the title for this part of the Document
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title for this part of the Document.
   *
   * @param title - the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return the list of chapters in this part of the Document
   */
  public List<Chapter> getChapters() {
    return chapters;
  }

  /**
   * Sets the list of chapters in this part of the Document.
   *
   * @param chapters - the list of chapters to set
   */
  public void setChapters(List<Chapter> chapters) {
    this.chapters = chapters;
  }

}
