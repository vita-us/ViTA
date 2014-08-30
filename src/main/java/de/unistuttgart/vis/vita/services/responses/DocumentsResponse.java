package de.unistuttgart.vis.vita.services.responses;

import java.util.List;

import de.unistuttgart.vis.vita.model.document.Document;

/**
 * Holds a list of all documents and its count.
 */
public class DocumentsResponse {
  
  private List<Document> documents;
  
  /**
   * Creates a new DocumentsResponse including all documents and the amount of documents.
   * 
   * @param pDocuments - the documents to be stored in the response
   */
  public DocumentsResponse(List<Document> pDocuments) {
    this.documents = pDocuments;
  }

  /**
   * @return the total amount of documents
   */
  public int getTotalCount() {
    return documents.size();
  }
  
  /**
   * @return all documents stored in this DocumentsResponse
   */
  public List<Document> getDocuments() {
    return documents;
  }
  
}
