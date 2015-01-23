/*
 * EnumParameter.java
 *
 */

package de.unistuttgart.vis.vita.services.responses.parameters;

import de.unistuttgart.vis.vita.model.document.EnumNLP;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
@XmlRootElement
public class EnumParameter extends AbstractParameter {

  private String defaultValue;
  private List<EnumNLP> values;

  public EnumParameter() {
  }

  public EnumParameter(String name, String attributeType) {
    super(name, attributeType);
    values = new ArrayList<>();
  }

  public List<EnumNLP> getValues() {
    return values;
  }

  public void setValues(List<EnumNLP> values) {
    this.values = values;
  }

  @Override
  public Object getDefaultValue() {
    return defaultValue;
  }

  @Override
  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }
}
