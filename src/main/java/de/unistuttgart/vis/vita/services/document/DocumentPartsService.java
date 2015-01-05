package de.unistuttgart.vis.vita.services.document;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.dao.DocumentPartDao;
import de.unistuttgart.vis.vita.services.responses.DocumentPartsResponse;

/**
 * Provides a method to GET all parts of the document this service refers to.
 */
@ManagedBean
public class DocumentPartsService {
  
  private String documentId;

  @EJB(name = "documentPartDao")
  private DocumentPartDao documentPartDao;

  /**
   * Sets the id of the document for which this service should provide the DocumentParts.
   * 
   * @param docId - the id of the document
   * @return this PartsService
   */
  public DocumentPartsService setDocumentId(String docId) {
    this.documentId = docId;
    return this;
  }
  
  /**
   * Returns a PartsResponse including a list of DocumentParts in current document.
   * 
   * @return PartsResponse including a list of DocumentParts
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public DocumentPartsResponse getParts() {
    return new DocumentPartsResponse(documentPartDao.findPartsInDocument(documentId));
  }

}
