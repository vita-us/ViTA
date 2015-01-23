/*
 * EnumNLP.java
 *
 */

package de.unistuttgart.vis.vita.model.document;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * The possible nlp tools.
 */
public enum EnumNLP {
  ANNIE("annie", "ANNIE"),
  STANFORD("stanford", "Stanford"),
  OPENNLP("opennlp", "OpenNLP");

  private final String name;
  private final String label;

  EnumNLP(String parameterName, String label) {
    this.name = parameterName;
    this.label = label;
  }

  public String getName() {
    return name;
  }

  public String getLabel() {
    return label;
  }
}
