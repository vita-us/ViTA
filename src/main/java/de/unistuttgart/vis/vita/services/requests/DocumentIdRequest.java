package de.unistuttgart.vis.vita.services.requests;

import javax.xml.bind.annotation.XmlElement;

public class DocumentIdRequest {

  @XmlElement
  private String id;
  
  /**
   * Creates a new Instance of DocumentIdResponse, setting id to default value.
   */
  public DocumentIdRequest() {
    
  }
  
  /**
   * Creates a new DocumentIdRequest setting the id to the given one.
   * 
   * @param documentId - the id of the document this request refers to
   */
  public DocumentIdRequest(String documentId) {
    this.id = documentId;
  }

  /**
   * @return the id of the document this request refers to
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id to which this request should refer to.
   * 
   * @param documentId - the id of the document to which this request should refer to
   */
  public void setId(String documentId) {
    this.id = documentId;
  }

}
