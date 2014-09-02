package de.unistuttgart.vis.vita.services.responses;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.unistuttgart.vis.vita.model.document.Document;

/**
 * Holds a list of all documents and its count.
 */
@XmlRootElement
public class DocumentsResponse {
  
  @XmlElementWrapper(name = "documents")
  @XmlElement(name = "document")
  private List<Document> documents;
  private int totalCount;

  /**
   * Creates a new instance of DocumentResponse, setting all attributes to default values.
   */
  public DocumentsResponse() {
    // must have a non-argument constructor
  }
  
  /**
   * Creates a new DocumentsResponse including all documents and the amount of documents.
   * 
   * @param pDocuments - the documents to be stored in the response
   */
  public DocumentsResponse(List<Document> pDocuments) {
    this.documents = pDocuments;
    this.totalCount = pDocuments.size();
  }

  /**
   * @return the total amount of documents
   */
  public int getTotalCount() {
    return totalCount;
  }
  
  /**
   * Sets the total count of Documents for this response.
   * 
   * @param totalCount - the number of documents
   */
  public void setTotalCount(int totalCount) {
    this.totalCount = totalCount;
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
  public void setDocument(List<Document> docList) {
    this.documents = docList;
    this.totalCount = docList.size();
  }
  
}
