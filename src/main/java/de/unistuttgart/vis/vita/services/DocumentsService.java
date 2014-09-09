package de.unistuttgart.vis.vita.services;

import java.util.ArrayList;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.services.responses.DocumentIdResponse;
import de.unistuttgart.vis.vita.services.responses.DocumentsResponse;

/**
 * A service offering a list of documents and the possibility to add new Documents.
 */
@Path("/documents")
@ManagedBean
public class DocumentsService {
  private EntityManager em;
  
  @Context
  private ResourceContext resourceContext;
  
  @Inject
  public DocumentsService(Model model) {
    em = model.getEntityManager();
  }

  /**
   * Returns a DocumentsResponse including a list of Documents with a given maximum length,
   * starting at an also given offset.
   * 
   * @param offset - the first Document to be returned
   * @param count - the maximum amount of Documents to be returned
   * @return DocumentResponse including a list of Documents
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public DocumentsResponse getDocumentsAsJSON(@QueryParam("offset") int offset,
                                              @QueryParam("count") int count) {
    TypedQuery<Document> query = em.createNamedQuery("Document.findAllDocuments", Document.class);
    query.setFirstResult(offset);
    query.setMaxResults(count);
    
    ArrayList<Document> readDocuments = new ArrayList<>();
    readDocuments.addAll(query.getResultList());

    return new DocumentsResponse(readDocuments);
  }
  
  /**
   * Adds a new Document.
   * 
   * @return a NewDocumentResponse including the id of the new Document
   */
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public DocumentIdResponse addDocument() {
    // TODO not implemented yet!
    return null;
  }
  
  /**
   * Returns the Service to access the Document with the given id.
   * 
   * @param id - the id of the document to be accessed
   * @return the DocumentService to access the given document
   */
  @Path("{documentId}")
  public DocumentService  getDocument(@PathParam("documentId") String id) {
    resourceContext.getResource(DocumentsService.class);
    return resourceContext.getResource(DocumentService.class).setId(id);
  }

}
