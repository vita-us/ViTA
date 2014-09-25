package de.unistuttgart.vis.vita.services;

import javax.annotation.ManagedBean;
import javax.ws.rs.Path;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;

import de.unistuttgart.vis.vita.services.occurrence.EntityOccurrencesService;

/**
 * Redirects attribute requests for the current entity to the right AttributeService.
 */
@ManagedBean
public class EntityService {
  
  private String entityId;
  
  @Context
  private ResourceContext resourceContext;

  /**
   * Sets the id of the Entity this service should refer to
   * 
   * @param eId - the id of the Entity which this EntityService should refer to
   */
  public EntityService setEntityId(String eId) {
    this.entityId = eId;
    return this;
  }

  /**
   * Returns the AttributeService for the current Entity.
   * 
   * @return the AttributeService answering the request
   */
  @Path("/attributes")
  public AttributeService getAttributes() {
    return resourceContext.getResource(AttributeService.class).setAttributeId(entityId);
  }
  
  /**
   * Returns the EntityOccurrencesService for the current Entity.
   * 
   * @return the EntityOccurrencesService which answering the request
   */
  @Path("/occurrences")
  public EntityOccurrencesService getOccurrences() {
    return resourceContext.getResource(EntityOccurrencesService.class).setEntityId(entityId);
  }

}
