/*
 * AbstractParameter.java
 *
 */

package de.unistuttgart.vis.vita.services.responses.parameters;

import java.lang.reflect.Type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({MinMaxParameter.class, BooleanParameter.class})
public abstract class AbstractParameter {

  protected String name;
  protected Type attributeType;

  public AbstractParameter() {
  }

  public AbstractParameter(String name, Type attributeType) {
    this.name = name;
    this.attributeType = attributeType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Type getType() {
    return attributeType;
  }

  public void setType(Type type) {
    this.attributeType = type;
  }
}
