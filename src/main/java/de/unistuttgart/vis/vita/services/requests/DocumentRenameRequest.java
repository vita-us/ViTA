package de.unistuttgart.vis.vita.services.requests;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Holds all data for requesting a document to be renamed.
 */
@XmlRootElement
public class DocumentRenameRequest extends DocumentIdRequest {
  
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
    super(documentId);
    this.name = newName;
  }

  /**
   * @return the name the referring document should be renamed into
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name to which the referring document should be renamed into
   * 
   * @param documentName - the new name for the referring document
   */
  public void setName(String documentName) {
    this.name = documentName;
  }

}
