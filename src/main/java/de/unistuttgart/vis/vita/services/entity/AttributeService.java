package de.unistuttgart.vis.vita.services.entity;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.services.occurrence.AttributeOccurrencesService;
import de.unistuttgart.vis.vita.services.responses.BasicAttribute;

/**
 * Provides methods to GET an attribute with current id
 */
@ManagedBean
public class AttributeService {
  @Inject
  private EntityManager em;
  
  private String documentId;
  private String entityId;
  private String attributeId;
  
  @Inject
  private AttributeOccurrencesService attributeOccurrencesService;

  /**
   * Sets the id of the document this service refers to and returns itself.
   * 
   * @param docId - the id of the document for which this service should offer Attributes.
   * @return this AttributeService
   */
  public AttributeService setDocumentId(String docId) {
    this.documentId = docId;
    return this;
  }

  /**
   * Sets the id of the entity with this attribute and returns itself.
   * 
   * @param eId - the id of the entity having this attribute
   * @return this AttributeService
   */
  public AttributeService setEntityId(String eId) {
    this.entityId = eId;
    return this;
  }

  /**
   * Sets the id of the Attribute this service should represent and returns itself.
   * 
   * @param attrId - the id of the Attribute this service should represent
   * @return this AttributeService
   */
  public AttributeService setAttributeId(String attrId) {
    this.attributeId = attrId;
    return this;
  }

  /**
   * Reads the requested attribute from database, returns it in JSON using Rest
   * 
   * @return attribute with the current id in JSON
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public BasicAttribute getAttribute() {
    Attribute basicAttribute = null;
    
    try {
      basicAttribute = readAttributeFromDatabase();
    } catch (NoResultException e) {
      throw new WebApplicationException(e, Response.status(Response.Status.NOT_FOUND).build());
    }
    
    return basicAttribute.toBasicAttribute();
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
    return attributeOccurrencesService
                                        .setDocumentId(documentId)
                                        .setAttributeId(attributeId)
                                        .setEntityId(entityId);
  }

}
