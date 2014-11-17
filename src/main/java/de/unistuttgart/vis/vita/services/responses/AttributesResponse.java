package de.unistuttgart.vis.vita.services.responses;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Holds a list of Attributes and its count.
 */
@XmlRootElement
public class AttributesResponse extends AbstractListResponse {
  
  @XmlElement(name = "attributes")
  private List<BasicAttribute> attributes;

  /**
   * Creates a new instance of AttributesResponse, setting all attributes to default values.
   */
  public AttributesResponse() {
    // must have a non-argument constructor
  }

  /**
   * Creates a new AttributeResponse holding the given list of Attributes and their count.
   * 
   * @param attributeList - a list of Attributes to be sent in this response
   */
  public AttributesResponse(List<BasicAttribute> attributeList) {
    super(attributeList.size());
    this.attributes = attributeList;
  }

  /**
   * Sets the list of Attributes hold in this response.
   * 
   * @param attributeList - the list of Attributes to be sent with this response
   */
  public void setAttributes(List<BasicAttribute> attributeList) {
    this.totalCount = attributeList.size();
    this.attributes = attributeList;
  }
  
  /**
   * @return list of attributes hold in this response
   */
  public List<BasicAttribute> getAttributes() {
    return attributes;
  }

}
