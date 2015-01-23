/*
 * AbstractParameter.java
 *
 */

package de.unistuttgart.vis.vita.services.responses.parameters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * Abstract class for response parameters. Only used for the service.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({MinMaxParameter.class, BooleanParameter.class, EnumParameter.class})
public abstract class AbstractParameter {

  protected String name;
  protected String attributeType;
  protected String description;
  protected String label;

  public AbstractParameter() {
  }

  public AbstractParameter(String name, String attributeType) {
    this.name = name;
    this.attributeType = attributeType;
  }

  public abstract Object getDefaultValue();

  public abstract void setDefaultValue(String defaultValue);

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getAttributeType() {
    return attributeType;
  }

  public void setAttributeType(String attributeType) {
    this.attributeType = attributeType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
