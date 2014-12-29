package de.unistuttgart.vis.vita.model.document;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;

/**
 * Represents a group of chapters, usually called "part" or "book" in a document.
 */
@Entity
@Table(indexes={
  @Index(columnList="number")
})
@NamedQueries({
    @NamedQuery(name = "DocumentPart.findAllParts",
                query = "SELECT dp "
                      + "FROM DocumentPart dp"),

    @NamedQuery(name = "DocumentPart.findPartsInDocument",
                query = "SELECT dp "
                      + "FROM DocumentPart dp, Document d "
                      + "WHERE d.id = :documentId "
                      + "AND dp MEMBER OF d.content.parts "
                      + "ORDER BY dp.number"),

    @NamedQuery(name = "DocumentPart.findPartById",
                query = "SELECT dp "
                      + "FROM DocumentPart dp "
                      + "WHERE dp.id = :partId"),

    @NamedQuery(name = "DocumentPart.findPartByTitle",
                query = "SELECT dp "
                      + "FROM DocumentPart dp "
                      + "WHERE dp.title = :partTitle")})
@XmlRootElement
public class DocumentPart extends AbstractEntityBase {

  private int number;

  @Column(length = 1000)
  private String title;

  @OneToMany
  @XmlElement(name = "chapters")
  @OrderColumn(name = "number")
  private List<Chapter> chapters = new ArrayList<Chapter>();

  /**
   * Creates a new instance of DocumentPart, setting all attributes to default values.
   */
  public DocumentPart() {
    this.chapters = new ArrayList<>();
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
   * @param newNumber - the number, starting from 1
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

}
