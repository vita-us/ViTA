/*
 * ParametersResponse.java
 *
 */

package de.unistuttgart.vis.vita.services.responses.parameters;

import de.unistuttgart.vis.vita.services.responses.AbstractListResponse;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
@XmlRootElement
public class ParametersResponse extends AbstractListResponse {

  @XmlElement(name = "parameters")
  private List<AbstractParameter> parameters;

  public ParametersResponse() {
  }

  public ParametersResponse(List<AbstractParameter> parameters) {
    super(parameters.size());
    this.parameters = parameters;
  }

  public List<AbstractParameter> getParameters() {
    return parameters;
  }

  public void setParameters(List<AbstractParameter> parameters) {
    this.parameters = parameters;
    setTotalCount(parameters.size());
  }
}
