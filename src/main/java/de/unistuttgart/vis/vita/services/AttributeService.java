package de.unistuttgart.vis.vita.services;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.services.occurrence.AttributeOccurrencesService;

/**
 * Provides methods to GET an attribute with current id
 */
@ManagedBean
public class AttributeService {

  private EntityManager em;
  
  private String attributeId;
  private String entityId;
  
  @Context
  private ResourceContext resourceContext;

  /**
   * Create new AttributeService and inject Model.
   * 
   * @param model - the injected Model
   */
  @Inject
  public AttributeService(Model model) {
    em = model.getEntityManager();
  }

  /**
   * Sets the id of the attribute this resource should represent and returns itself.
   * 
   * @param attId the id
   * @return AttributeService
   */
  public AttributeService setAttributeId(String attId) {
    this.attributeId = attId;
    return this;
  }

  /**
   * Sets the id of the entity with this attribute and returns itself.
   * 
   * @param eId - the id of the entity having this attribute
   * @return AttributeService
   */
  public AttributeService setEntityId(String eId) {
    this.entityId = eId;
    return this;
  }

  /**
   * Reads the requested attribute from database, returns it in JSON using Rest
   * 
   * @return attribute with the current id in JSON
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Attribute getAttribute() {
    Attribute attr = null;
    
    try {
      attr = readAttributeFromDatabase();
    } catch (NoResultException e) {
      throw new WebApplicationException(e, Response.status(Response.Status.NOT_FOUND).build());
    }
    
    return attr;
  }

  private Attribute readAttributeFromDatabase() {
    TypedQuery<Attribute> query = em.createNamedQuery("Attribute.findAttributeById", 
                                                      Attribute.class);
    query.setParameter("attributeId", attributeId);
    return query.getSingleResult();
  }
  
  /**
   * Returns the AttributeOccurrencesService for the current attribute and entity.
   * 
   * @return the AttributeOccurrencesService answering this request
   */
  @Path("/occurrences")
  public AttributeOccurrencesService getOccurrences() {
    return resourceContext.getResource(AttributeOccurrencesService.class)
                                        .setAttributeId(attributeId)
                                        .setEntityId(entityId);
  }

}
