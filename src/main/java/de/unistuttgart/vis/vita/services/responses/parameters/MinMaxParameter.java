/*
 * MinMaxParameter.java
 *
 */

package de.unistuttgart.vis.vita.services.responses.parameters;

import java.lang.reflect.Type;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
@XmlRootElement
public class MinMaxParameter extends AbstractParameter {

  private long min;
  private long max;

  public MinMaxParameter() {
  }

  public MinMaxParameter(String name, Type type, String description, long min, long max) {
    super(name, type, description);
    this.min = min;
    this.max = max;
  }

  public long getMin() {
    return min;
  }

  public void setMin(long min) {
    this.min = min;
  }

  public long getMax() {
    return max;
  }

  public void setMax(long max) {
    this.max = max;
  }
}
