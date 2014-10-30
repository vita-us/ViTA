package de.unistuttgart.vis.vita.services.responses;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A flat representation of Attribute, without Occurrences.
 */
@XmlRootElement
public class BasicAttribute {

  private String id;
  private String type;
  private String content;
  
  /**
   * Creates a new instance of BasicAttribute, setting all fields to default values.
   */
  public BasicAttribute() {
    // zero-argument constructor needed
  }
  
  /**
   * Creates a new BasicAttribute with given id, type and content.
   * 
   * @param id - the id of the real Attribute this represents
   * @param type - the textual representation of the AttributeType
   * @param content - the content of the Attribute this represents
   */
  public BasicAttribute(String id, String type, String content) {
    this.id = id;
    this.type = type;
    this.content = content;
  }

  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getType() {
    return type;
  }
  
  public void setType(String type) {
    this.type = type;
  }
  
  public String getContent() {
    return content;
  }
  
  public void setContent(String content) {
    this.content = content;
  }
  
}
