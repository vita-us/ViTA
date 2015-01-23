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
//@JsonSerialize(using = EnumNLPSerializer.class)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
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

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("label")
  public String getLabel() {
    return label;
  }
}

/**
 * For correct serialization of the enum values.
 */
class EnumNLPSerializer extends JsonSerializer<EnumNLP> {

  @Override
  public void serialize(EnumNLP value, JsonGenerator generator,
                        SerializerProvider provider) throws IOException,
                                                            JsonProcessingException {

    generator.writeStartObject();
    generator.writeFieldName("name");
    generator.writeNumber(value.getName());
    generator.writeFieldName("label");
    generator.writeString(value.getLabel());
    generator.writeEndObject();
  }
}
