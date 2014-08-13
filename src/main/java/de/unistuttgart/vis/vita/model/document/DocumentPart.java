package de.unistuttgart.vis.vita.model.document;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Represents a part of a Document including number, title and chapters of this DocumentPart.
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
   * @return the number of this part of the Document
   */
  public int getNumber() {
    return number;
  }

  /**
   * Sets the part number.
   *
   * @param number - the number of this part of the Document
   */
  public void setNumber(int number) {
    this.number = number;
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
