package de.unistuttgart.vis.vita.services;

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.services.responses.DocumentsResponse;
import de.unistuttgart.vis.vita.services.responses.DocumentIdResponse;

/**
 * A service offering a list of documents and the possibility to add new Documents.
 */
@Path("/documents")
public class DocumentsService {
  
  @Context
  UriInfo uriInfo;
  @Context
  Request request;
  
  private EntityManager em;

  /**
   * Creates a new DocumentService.
   */
  public DocumentsService() {
    this.em = new Model().getEntityManager();
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
  @Produces({MediaType.APPLICATION_JSON})
  public DocumentsResponse getDocumentsAsJSON(@QueryParam("offset") int offset,
                                              @QueryParam("count") int count) {
    // TODO not implemented yet!
    return new DocumentsResponse(new ArrayList<Document>());
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
    return new DocumentService(uriInfo, request, id);
  }

}
