package de.unistuttgart.vis.vita.services;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.services.requests.DocumentRenameRequest;

/**
 * Provides methods for GET, PUT and DELETE a document with a specific id.
 */
public class DocumentService {
  
  @Context
  UriInfo uriInfo;
  @Context
  Request request;
  String id;
  
  private EntityManager em;
  
  /**
   * Creates a new DocumentService with given uri information, request and document id.
   * 
   * @param uriInfo
   * @param request
   * @param id
   */
  public DocumentService(UriInfo uriInfo, Request request, String id) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.id = id;
    this.em = Model.createUnitTestModelWithoutDrop().getEntityManager();
  }
  
  /**
   * Reads the document from the database and returns it.
   * 
   * @return the document with the current id
   */
  private Document readDocumentFromDatabase()  {
    TypedQuery<Document> query = em.createNamedQuery("Document.findDocumentById", Document.class);
    query.setParameter("documentId", id);
    return query.getSingleResult();
  }
  
  /**
   * Reads the requested document from the database and returns it in JSON using the REST.
   * 
   * @return the document with the current id in JSON
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Document getDocument() {
    return readDocumentFromDatabase();
  }
  
  /**
   * Renames the current document to the given name.
   * 
   * @param renameRequest - the renaming request including id and new name.
   * @return a response with no content if renaming was successful, HTTP 404 if document was not
   * found or 500 if an error occurred.
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  public Response putDocument(DocumentRenameRequest renameRequest) {
    Response response = null;
    try {
      em.getTransaction().begin();
      Document affectedDocument = readDocumentFromDatabase();
      affectedDocument.getMetadata().setTitle(renameRequest.getName());
      em.persist(affectedDocument);
      em.getTransaction().commit();
      
      response = Response.noContent().build();
    } catch (EntityNotFoundException enfe) {
      response = Response.status(404).build();
    } catch (RollbackException re) {
      response =  Response.serverError().build();
    }
    return response;
  }
  
  /**
   * Deletes the document with the current id.
   * 
   * @return a response with no content if removal was successful, status 404 if document was not
   * found
   */
  @DELETE
  public Response deleteDocument() {
    Response response = null;
    try {
      em.getTransaction().begin();
      em.remove(readDocumentFromDatabase());
      em.getTransaction().commit();
      
      response = Response.noContent().build();
    } catch (IllegalArgumentException iae) {
      response = Response.status(404).build();
    }
    return response;
  }
  
  /**
   * Returns the ProgressService for the current document.
   * 
   * @return progress service which answers this request
   */
  @Path("/progress")
  public ProgressService getProgress() {
    return new ProgressService(uriInfo, request, id);
  }
  
  /**
   * Returns the ChapterService for the current document and given chapter id.
   * 
   * @param chapterId - the chapter id given in the path
   * @return the chapter service which answers this request
   */
  @Path("/chapters/{chapterId}")
  public ChapterService getChapters(@PathParam("chapterId") String chapterId) {
    return new ChapterService(uriInfo, request, chapterId);
  }

}
