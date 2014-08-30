package de.unistuttgart.vis.vita.services;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.services.responses.DocumentIdResponse;
import de.unistuttgart.vis.vita.services.responses.DocumentRenameResponse;

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
    this.em = new Model().getEntityManager();
  }
  
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Document getDocument() {
    // TODO not implemented yet!
    return null;
  }
  
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  public DocumentRenameResponse putDocument() {
    // TODO not implemented yet!
    return null;
  }
  
  @DELETE
  public DocumentIdResponse deleteDocument() {
    // TODO not implemented yet!
    return null;
  }

}
