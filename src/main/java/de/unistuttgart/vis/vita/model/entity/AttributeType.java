package de.unistuttgart.vis.vita.model.entity;

/**
 * Represents the type of an Attribute.
 */
public enum AttributeType {
  AGE, 
  NAME, 
  SIZE, 
  UNKNOWN;

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
      default:
        textualRepresentation = "Unknown";
    }
    return textualRepresentation;
  }
}
