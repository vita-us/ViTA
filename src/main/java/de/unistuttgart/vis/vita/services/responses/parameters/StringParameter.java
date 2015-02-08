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
public class StringParameter extends AbstractParameter {

  protected String defaultValue;

  public StringParameter() {
  }

  public StringParameter(String name) {
    super(name, "string");
  }

  @Override
  public Object getDefaultValue() {
    return defaultValue;
  }

  @Override
  public void setDefaultValue(Object defaultValue) {
    this.defaultValue = defaultValue.toString();
  }
}
