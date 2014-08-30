package de.unistuttgart.vis.vita.services.responses;

/**
 * A response indicating the successful renaming of a document, including id and new name of the
 * renamed document.
 */
public class DocumentRenameResponse extends DocumentIdResponse {
  
  private String name;

  /**
   * Creates a new response for the renaming of a document, setting all attributes to default
   * values.
   */
  public DocumentRenameResponse() {
    // obligatory non-argument constructor
  }

  /**
   * Creates a new response for the renaming of a document, setting id and name to the given values.
   * 
   * @param pId - the id of the document which was renamed
   * @param newName - the new name of the document
   */
  public DocumentRenameResponse(String pId, String newName) {
    super(pId);
    this.name = newName;
  }

  /**
   * @return the new name of the document
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the new name of the document.
   * 
   * @param newName - the new name of the renamed document
   */
  public void setName(String newName) {
    this.name = newName;
  }

}
