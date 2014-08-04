package de.unistuttgart.vis.vita.model.entity;

import de.unistuttgart.vis.vita.model.document.TextSpan;

import java.util.Set;
import java.util.TreeSet;

/**
 * Represents one attribute of an entity found in the document. This contains an id, type, content
 * and a set of occurrences in the document.
 */
public class Attribute {

  private int id;
  private AttributeType type;
  private String content;
  private Set<TextSpan> occurrences;

  /**
   * Creates a new attribute, setting all fields to default values.
   */
  public Attribute() {
    occurrences = new TreeSet<>();
  }

  /**
   * Creates a new Attribute with given id, type and content.
   *
   * @param pId      - the id for the new attribute
   * @param pType    - the type of the new attribute, for example 'Name'
   * @param pContent - the content of the new attribute, for example 'Bilbo Baggins'
   */
  public Attribute(int pId, AttributeType pType, String pContent) {
    this.id = pId;
    this.type = pType;
    this.content = pContent;
    occurrences = new TreeSet<>();
  }

  /**
   * @return the id of this Attribute
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the id of this Attribute to the given value
   *
   * @param newId - the new id for this Attribute
   */
  public void setId(int newId) {
    this.id = newId;
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
  public Set<TextSpan> getOccurrences() {
    return occurrences;
  }

  /**
   * Sets the occurrences related to this Attribute.
   *
   * @param occurrences - a set of all occurrences related to this Attribute
   */
  public void setOccurrences(Set<TextSpan> occurrences) {
    this.occurrences = occurrences;
  }

}
