package de.unistuttgart.vis.vita.model.entity;

import javax.xml.bind.annotation.XmlEnumValue;

/**
 * Represents the type of an Attribute.
 */
public enum AttributeType {
  
  @XmlEnumValue("age") AGE, 
  @XmlEnumValue("name") NAME, 
  @XmlEnumValue("size") SIZE,
  @XmlEnumValue("gender") GENDER,
  @XmlEnumValue("unknown") UNKNOWN;

  @Override
  public String toString() {
    String textualRepresentation;
    switch (this) {
      case AGE:
        textualRepresentation = "Age";
        break;
      case NAME:
        textualRepresentation = "Name";
        break;
      case SIZE:
        textualRepresentation = "Size";
        break;
      case GENDER:
        textualRepresentation = "Gender";
        break;
      default:
        textualRepresentation = "Unknown";
    }
    return textualRepresentation;
  }
}
