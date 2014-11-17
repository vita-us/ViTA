package de.unistuttgart.vis.vita.services.entity;

import javax.annotation.ManagedBean;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;

/**
 * Redirects entity and relations requests for the current Document to the right sub service.
 */
@ManagedBean
public class EntitiesService {
  
  private String documentId;
  
  @Context
  private ResourceContext resourceContext;
  
  /**
   * Sets the id of the Document this service should refer to
   * 
   * @param docId - the id of the Document which this EntitiesService should refer to
   */
  public EntitiesService setDocumentId(String docId) {
    this.documentId = docId;
    return this;
  }
  
  /**
   * Returns the Service to access the Entity with the given id.
   * 
   * @param id - the id of the Entity to be accessed
   * @return the EntityService to access the given Entity with the given id
   */
  @Path("{entityId}")
  public EntityService getEntity(@PathParam("entityId") String id) {
    return resourceContext.getResource(EntityService.class).setEntityId(id)
                                                            .setDocumentId(documentId);
  }
  
  /**
   * Returns the RelationsService for the current document.
   * 
   * @return the RelationsService which answers the request
   */
  @Path("/relations")
  public EntityRelationsService getRelations() {
    return resourceContext.getResource(EntityRelationsService.class).setDocumentId(documentId);
  }

}
