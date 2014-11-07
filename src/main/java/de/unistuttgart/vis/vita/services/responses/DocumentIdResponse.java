package de.unistuttgart.vis.vita.services.responses;

/**
 * A response which indicates the successful creation of a document, including the id under which
 * the new document can accessed.
 */
public class DocumentIdResponse {

  private String id;

  /**
   * Creates a new response for the successful creation of a document, setting the id to null.
   */
  public DocumentIdResponse() {
    // obligatory non-argument constructor
  }

  /**
   * Creates a new response for the successful creation of a document, setting the id to the given
   * value.
   * 
   * @param pId - the id under which the new created document can be accessed
   */
  public DocumentIdResponse(String pId) {
    this.id = pId;
  }

  /**
   * @return id of the new created document
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id under which the new created document can be accessed.
   * 
   * @param newId - the new id under which the new created document can be accessed
   */
  public void setId(String newId) {
    this.id = newId;
  }

}
