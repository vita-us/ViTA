package de.unistuttgart.vis.vita.model.document;

import java.util.List;

/**
 * Represents a part of a Document including number, title and chapters of this DocumentPart.
 */
public class DocumentPart {

  // attributes
  private int number;
  private String title;
  private List<Chapter> chapters;

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
