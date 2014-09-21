package de.unistuttgart.vis.vita.services;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.entity.Attribute;

/**
 * Provides methods to GET an attribute with current id
 */
public class AttributeService {

  private EntityManager em;
  private String attributeId;

  @Inject
  public AttributeService(Model model) {
    em = model.getEntityManager();
  }

  /**
   * Sets the id of the attribute this resource should represent
   * 
   * @param id the id
   */
  public AttributeService setAttributeId(String id) {
    this.attributeId = id;
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

}
