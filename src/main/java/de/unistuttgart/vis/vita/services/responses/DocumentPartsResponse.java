package de.unistuttgart.vis.vita.services.responses;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.unistuttgart.vis.vita.model.document.DocumentPart;

/**
 * Holds a list of DocumentParts and its count.
 */
@XmlRootElement
public class DocumentPartsResponse extends AbstractListResponse {
  
  @XmlElement(name = "parts")
  private List<DocumentPart> parts;
  
  /**
   * Creates a new instance of DocumentPartsResponse, setting all attributes to default values.
   */
  public DocumentPartsResponse() {
    // must have a non-argument constructor
  }
  
  /**
   * Creates a new DocumentPartsResponse including all parts of a Document and their count.
   * 
   * @param partsList - the parts to be stored in the response
   */
  public DocumentPartsResponse(List<DocumentPart> partsList) {
    super(partsList.size());
    this.setParts(partsList);
  }

  /**
   * @return all DocumentParts stored in this response
   */
  public List<DocumentPart> getParts() {
    return parts;
  }

  /**
   * Sets the List of DocumentParts for this response.
   * 
   * @param partsList - the list of DocumentParts to be stored
   */
  public void setParts(List<DocumentPart> partsList) {
    this.totalCount = partsList.size();
    this.parts = partsList;
  }

}
