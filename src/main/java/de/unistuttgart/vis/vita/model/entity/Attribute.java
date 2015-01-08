package de.unistuttgart.vis.vita.model.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.services.responses.BasicAttribute;

/**
 * Represents one attribute of an entity found in the document. This contains an id, type, content
 * and a set of occurrences in the document.
 */
@javax.persistence.Entity
@Table(indexes={
    @Index(columnList="type")
})
public class Attribute extends AbstractEntityBase {

  // there is another "type" in Entity, so this must be named differently!
  @XmlElement(name = "attributetype", required= true)
  private AttributeType type;

  @Column(length = 1000)
  private String content;

  @OneToMany(cascade = CascadeType.ALL)
  @OrderBy("start.offset ASC")
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
   * @param pType - the type of the new attribute, for example 'Name'
   * @param pContent - the content of the new attribute, for example 'Bilbo Baggins'
   */
  public Attribute(AttributeType pType, String pContent) {
    this();
    this.type = pType;
    this.content = pContent;
  }

  @Override
  public String toString() {
    return "Attribute{" +
           "type=" + type +
           ", size=" + occurrences.size() +
           ", content='" + content + '\'' +
           '}';
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
   * Creates a flat representation of this Attribute without occurrences.
   *
   * @return BasicAttribute representing this Attribute
   */
  public BasicAttribute toBasicAttribute() {
    return new BasicAttribute(getId(), type.toString(), content);
  }

  /**
   * Merges the occurrences of all attributes with the same type and content
   * @param attributes
   * @return
   */
  public static Set<Attribute> merge(Iterable<Attribute> attributes) {
    Set<Attribute> result = new HashSet<>();
    for (Attribute attr : attributes) {
      boolean exists = false;
      for (Attribute existing : result) {
        if (existing.getType() == attr.getType()
            && existing.getContent().equals(attr.getContent())) {
          existing.getOccurrences().addAll(attr.getOccurrences());
          exists = true;
          break;
        }
      }
      if (!exists) {
        Attribute newAttr = new Attribute(attr.getType(), attr.getContent());
        newAttr.getOccurrences().addAll(attr.getOccurrences());
        result.add(newAttr);
      }
    }
    return result;
  }

}
