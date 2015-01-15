/*
 * BooleanParameter.java
 *
 */

package de.unistuttgart.vis.vita.services.responses.parameters;

import java.lang.reflect.Type;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Parameter which has no additional parameters because the value can only be true or false.
 */
@XmlRootElement
public class BooleanParameter extends AbstractParameter {

  public BooleanParameter() {
  }

  public BooleanParameter(String name, Type type) {
    super(name, type);
  }
}
