/*
 * EnumParameter.java
 *
 */

package de.unistuttgart.vis.vita.services.responses.parameters;

import de.unistuttgart.vis.vita.analysis.annotations.Label;
import de.unistuttgart.vis.vita.model.document.EnumNLP;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The enum parameters for the service to get all available parameters.
 */
@XmlRootElement
public class EnumParameter extends AbstractParameter {

  private String defaultValue;
  private List<MetaEnumParameter> values;

  public EnumParameter() {
  }

  public EnumParameter(String name, String attributeType) {
    super(name, attributeType);
    values = new ArrayList<>();
  }

  public List<MetaEnumParameter> getValues() {
    return values;
  }

  public void setValues(List<MetaEnumParameter> values) {
    this.values = values;
  }

  public void addValues(List<EnumNLP> val) {
    for (EnumNLP enumNLP : val) {
      try {
        String label = enumNLP.getClass().getField((enumNLP).name())
            .getAnnotation(Label.class).value();
        String name = enumNLP.getClass().getField((enumNLP).name())
            .getAnnotation(XmlEnumValue.class).value();
        values.add(new MetaEnumParameter(name, label));
      } catch (NoSuchFieldException e) {
        throw new IllegalStateException(
            "Could not find necessary annotations for the enum parameters.", e);
      }
    }
  }

  @Override
  public Object getDefaultValue() {
    return defaultValue;
  }

  @Override
  public void setDefaultValue(Object defaultValue) {
    this.defaultValue = ((EnumNLP)defaultValue).getName();
  }
}

/**
 * Representation of the enum to the service.
 */
class MetaEnumParameter {

  private String name;
  private String label;

  public MetaEnumParameter() {
  }

  public MetaEnumParameter(String name, String label) {
    this.name = name;
    this.label = label;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }
}
