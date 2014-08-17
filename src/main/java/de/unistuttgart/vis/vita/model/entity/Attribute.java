package de.unistuttgart.vis.vita.model.entity;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import de.unistuttgart.vis.vita.model.document.TextSpan;

/**
 * Represents one attribute of an entity found in the document. This contains an id, type, content
 * and a set of occurrences in the document.
 */
@javax.persistence.Entity
@NamedQueries({
  @NamedQuery(name = "Attribute.findAllAttributes",
      query = "SELECT a "
      + "FROM Attribute a"),
      
  @NamedQuery(name = "Attribute.findAttributeById",
      query = "SELECT a "
      + "FROM Attribute a "
      + "WHERE a.id = :attributeId"),
  
  @NamedQuery(name = "Attribute.findAttributeByType",
      query = "SELECT a "
      + "FROM Attribute a "
      + "WHERE a.type = :attributeType")
})
public class Attribute {

  @GeneratedValue
  @Id
  private int id;
  private AttributeType type;
  private String content;
  
  @OneToMany
  @OrderBy("START_OFFSET ASC")
  private SortedSet<TextSpan> occurrences;

  /**
   * Creates a new attribute, setting all fields to default values.
   */
  public Attribute() {
    occurrences = new TreeSet<>();
  }

  /**
   * Creates a new Attribute with given type and content.
   *
   * @param pId      - the id for the new attribute
   * @param pType    - the type of the new attribute, for example 'Name'
   * @param pContent - the content of the new attribute, for example 'Bilbo Baggins'
   */
  public Attribute(AttributeType pType, String pContent) {
    this();
    this.type = pType;
    this.content = pContent;
  }

  /**
   * @return the id of this Attribute
   */
  public int getId() {
    return id;
  }

  /**
   * @return the type of this Attribute
   */
  public AttributeType getType() {
    return type;
  }

  /**
   * @param newType the new type for the Attribute to set
   */
  public void setType(AttributeType newType) {
    this.type = newType;
  }

  /**
   * @return the content of this Attribute
   */
  public String getContent() {
    return content;
  }

  /**
   * Sets the content for this Attribute.
   *
   * @param newContent - the new content for this Attribute
   */
  public void setContent(String newContent) {
    this.content = newContent;
  }

  /**
   * @return a Set of all occurrences in the text which are related to this Attribute
   */
  public SortedSet<TextSpan> getOccurrences() {
    return occurrences;
  }

  /**
   * Sets the occurrences related to this Attribute.
   *
   * @param occurrences - a set of all occurrences related to this Attribute
   */
  public void setOccurrences(SortedSet<TextSpan> occurrences) {
    this.occurrences = occurrences;
  }

}
