/*
 * EnumNLP.java
 *
 */

package de.unistuttgart.vis.vita.model.document;


import de.unistuttgart.vis.vita.analysis.annotations.Label;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * The possible nlp tools.
 */
@XmlEnum
public enum EnumNLP {
  @XmlEnumValue("annie")
  @Label("ANNIE")
  ANNIE("annie"),
  @XmlEnumValue("stanford")
  @Label("Stanford")
  STANFORD("stanford"),
  @XmlEnumValue("opennlp")
  @Label("OpenNLP")
  OPENNLP("opennlp");

  private final String name;

  EnumNLP(String parameterName) {
    this.name = parameterName;
  }

  public String getName() {
    return name;
  }
}
