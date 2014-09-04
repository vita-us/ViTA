package de.unistuttgart.vis.vita.services.requests;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DocumentRenameRequest {
  
  @XmlElement
  private String id;
  @XmlElement
  private String name;
  
  /**
   * Creates a new request to change the name of a document, setting all attribute to default 
   * values.
   */
  public DocumentRenameRequest() {
    
  }
  
  /**
   * Creates a new request to change the name of a document with the given id to a also given 
   * name.
   * 
   * @param documentId - the id of the document which name should be changed.
   * @param newName - the new name for the document
   */
  public DocumentRenameRequest(String documentId, String newName) {
    this.id = documentId;
    this.name = newName;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
