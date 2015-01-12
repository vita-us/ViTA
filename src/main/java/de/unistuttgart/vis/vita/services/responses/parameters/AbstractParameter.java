/*
 * AbstractParameter.java
 *
 */

package de.unistuttgart.vis.vita.services.responses.parameters;

import java.lang.reflect.Type;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 */
@XmlRootElement
@XmlSeeAlso({MinMaxParameter.class, BooleanParameter.class})
public abstract class AbstractParameter {

  protected String name;
  protected Type type;

  public AbstractParameter() {
  }

  public AbstractParameter(String name, Type type) {
    this.name = name;
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }
}
