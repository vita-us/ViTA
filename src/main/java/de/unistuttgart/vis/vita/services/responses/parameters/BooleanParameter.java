/*
 * BooleanParameter.java
 *
 */

package de.unistuttgart.vis.vita.services.responses.parameters;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Parameter which has no additional parameters because the value can only be true or false.
 */
@XmlRootElement
public class BooleanParameter extends AbstractParameter {

  protected boolean defaultValue;

  public BooleanParameter() {
  }

  public BooleanParameter(String name, String type) {
    super(name, type);
  }

  @Override
  public Object getDefaultValue() {
    return defaultValue;
  }

  @Override
  public void setDefaultValue(Object defaultValue) {
    this.defaultValue = (boolean)defaultValue;
  }
}
