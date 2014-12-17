package de.unistuttgart.vis.vita.model.entity;

import de.unistuttgart.vis.vita.model.document.TextSpan;

import org.apache.commons.lang.StringUtils;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.unistuttgart.vis.vita.model.document.TextSpan;

import org.apache.commons.lang.StringUtils;

/**
 * The information about an entity that can be collected in the first pass
 * <p>
 * This class can not be persisted and should be converted to Entity for that.
 */
public class BasicEntity {
  private String displayName;
  private EntityType type;

  private SortedSet<Attribute> nameAttributes;
  private Set<Attribute> attributes;
  private SortedSet<TextSpan> occurrences;
  
  public BasicEntity() {
    nameAttributes = new TreeSet<>(new AttributeComaparator());
    occurrences = new TreeSet<>();
    attributes = new HashSet<>();
  }

  /**
   * Gets the name under which this entity will be shown
   * 
   * @return the name under which this entity will be shown
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Sets the name under which this entity is shown in the graphical user interface.
   * 
   * @param newDisplayName - the new name under which this entity should be shown
   */
  public void setDisplayName(String newDisplayName) {
    this.displayName = newDisplayName;
  }

  /**
   * Gets all the names under which the entity is known
   * 
   * @return the names under which the entity is known
   */
  public SortedSet<Attribute> getNameAttributes() {
    return nameAttributes;
  }

  /**
   * Sets the names under which the entity is known
   *
   * @param nameAttributes the names under which the entity is known
   */
  public void setNameAttributes(Set<Attribute> nameAttributes) {
    this.nameAttributes = new TreeSet<>(new AttributeComaparator());
    this.nameAttributes.addAll(nameAttributes);
  }

  /**
   * Gets all occurrences of this entity in the document
   * 
   * @return Set of all occurrences of this entity in the document
   */
  public SortedSet<TextSpan> getOccurences() {
    return occurrences;
  }

  /**
   * Sets the occurrences for this entity.
   * 
   * @param newOccurences - a set of new occurrences for this entity
   */
  public void setOccurences(SortedSet<TextSpan> newOccurences) {
    this.occurrences = newOccurences;
  }

  /**
   * Indicates of which type this entity is
   * 
   * @return either PERSON or PLACE
   */
  public EntityType getType() {
    return type;
  }

  /**
   * Sets of which type this entity is
   * 
   * @param type either PERSON or PLACE
   */
  public void setType(EntityType type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "BasicEntity{" +
           "type=" + type +
           ", displayName='" + displayName + '\'' +
           ", occurrences=" + StringUtils.join(occurrences, ", ") +
           ", namedAttributes=" + StringUtils.join(nameAttributes, ", ") +
           ", attributes=" + StringUtils.join(attributes, ", ") +
           '}';
  }

  public Set<Attribute> getAttributes() {
    return attributes;
  }

  public void setAttributes(Set<Attribute> attributes) {
    this.attributes = attributes;
  }
}

/**
 * Comparator for the names of the entity. Sorts them by the size.
 */
class AttributeComaparator implements Comparator<Attribute> {

  @Override
  public int compare(Attribute o1, Attribute o2) {
    return (o1.getOccurrences().size() > o2.getOccurrences().size() ? -1 : 1);
  }
}
