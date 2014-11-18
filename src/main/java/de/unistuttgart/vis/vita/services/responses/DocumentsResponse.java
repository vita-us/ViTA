package de.unistuttgart.vis.vita.services.responses;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.unistuttgart.vis.vita.model.document.Document;

/**
 * Holds a list of documents and its count.
 */
@XmlRootElement
public class DocumentsResponse extends AbstractListResponse {
  
  @XmlElement(name = "documents")
  private List<Document> documents;
  
  /**
   * Creates a new instance of DocumentResponse, setting all attributes to default values.
   */
  public DocumentsResponse() {
    // must have a non-argument constructor
  }
  
  /**
   * Creates a new DocumentsResponse including all documents and the amount of documents.
   * 
   * @param documentsToBeResponded - the documents to be stored in the response
   */
  public DocumentsResponse(List<Document> documentsToBeResponded) {
    super(documentsToBeResponded.size());
    this.documents = documentsToBeResponded;
  }

  /**
   * @return all documents stored in this DocumentsResponse
   */
  public List<Document> getDocuments() {
    return documents;
  }
  
  /**
   * Sets the List of Documents for this response.
   * 
   * @param docList - the list of documents
   */
  public void setDocuments(List<Document> docList) {
    this.documents = docList;
    this.totalCount = docList.size();
  }
  
}
