package de.unistuttgart.vis.vita.services.document;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.services.responses.DocumentPartsResponse;

/**
 * Provides a method to GET all parts of the document this service refers to.
 */
public class DocumentPartsService {
  
  private String documentId;

  @Inject
  private EntityManager em;

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
    return new DocumentPartsResponse(readPartsFromDatabase());
  }
  
  private List<DocumentPart> readPartsFromDatabase() {
    TypedQuery<DocumentPart> query = em.createNamedQuery("DocumentPart.findPartsInDocument",
        DocumentPart.class);
    query.setParameter("documentId", documentId);
    return query.getResultList();
  }

}