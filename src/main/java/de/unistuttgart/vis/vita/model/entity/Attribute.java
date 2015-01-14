package de.unistuttgart.vis.vita.model.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

import de.unistuttgart.vis.vita.model.document.Occurrence;
import de.unistuttgart.vis.vita.services.responses.BasicAttribute;

/**
 * Represents one attribute of an entity found in the document. This contains an id, type, content
 * and a set of occurrences in the document.
 */
@javax.persistence.Entity
@Table(indexes={
    @Index(columnList="type")
})
@NamedQueries({
    @NamedQuery(name = "Attribute.findAllAttributes",
                query = "SELECT a "
                      + "FROM Attribute a"),

    @NamedQuery(name = "Attribute.findAttributeById",
                query = "SELECT a "
                      + "FROM Attribute a "
                      + "WHERE a.id = :attributeId"),

    @NamedQuery(name = "Attribute.findAttributesForEntity",
                query = "SELECT a "
                      + "FROM Attribute a, Entity e "
                      + "WHERE e.id = :entityId "
                      + "AND a MEMBER OF e.attributes"),

    @NamedQuery(name = "Attribute.findAttributeByType",
                query = "SELECT a "
                      + "FROM Attribute a "
                      + "WHERE a.type = :attributeType")}
)
public class Attribute extends AbstractEntityBase {

  // there is another "type" in Entity, so this must be named differently!
  @XmlElement(name = "attributetype", required= true)
  private AttributeType type;

  @Column(length = 1000)
  private String content;

  @OneToMany(cascade = CascadeType.ALL)
  @OrderBy("start.offset ASC")
  private List<Occurrence> occurrences;

  /**
   * Creates a new attribute, setting all fields to default values.
   */
  public Attribute() {
    occurrences = new ArrayList<Occurrence>();
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
  public List<Occurrence> getOccurrences() {
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

}
