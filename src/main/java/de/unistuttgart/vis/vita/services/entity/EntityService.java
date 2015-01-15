package de.unistuttgart.vis.vita.services.entity;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.unistuttgart.vis.vita.model.dao.EntityDao;
import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.services.BaseService;
import de.unistuttgart.vis.vita.services.occurrence.EntityOccurrencesService;
import de.unistuttgart.vis.vita.services.search.SearchEntityService;

/**
 * Redirects attribute requests for the current entity to the right AttributeService.
 */
@ManagedBean
public class EntityService extends BaseService {
  
  private String documentId;
  private String entityId;

  private EntityDao entityDao;

  @Inject
  private AttributesService attributesService;

  @Inject
  private EntityOccurrencesService entityOccurrencesService;

  @Inject
  SearchEntityService searchEntityService;

  @Override public void postConstruct() {
    super.postConstruct();
    entityDao = getDaoFactory().getEntityDao();
  }

  /**
   * Sets the id of the Document the referring entity occurs in
   * 
   * @param docId - the id of the Document in which the referring entity occurs
   */
  public EntityService setDocumentId(String docId) {
    this.documentId = docId;
    return this;
  }

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
   * Reads the requested entity from the database and returns it in JSON using the REST.
   *
   * @return the entity with the current id in JSON
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Entity getEntity() {
    Entity readEntity = null;

    try {
      readEntity = readEntityFromDatabase();
    } catch (NoResultException e) {
      throw new WebApplicationException(e, Response.status(Response.Status.NOT_FOUND).build());
    }

    return readEntity;
  }

  private Entity readEntityFromDatabase() {
    return entityDao.findById(entityId);
  }

  /**
   * Returns the AttributeService for the current Entity.
   * 
   * @return the AttributeService answering the request
   */
  @Path("/attributes")
  public AttributesService getAttributes() {
    return attributesService.setDocumentId(documentId).setEntityId(entityId);
  }
  
  /**
   * Returns the EntityOccurrencesService for the current Entity.
   * 
   * @return the EntityOccurrencesService which answering the request
   */
  @Path("/occurrences")
  public EntityOccurrencesService getOccurrences() {
    return entityOccurrencesService.setEntityId(entityId).setDocumentId(documentId);
  }
  
  public SearchEntityService getSearch() {
    return searchEntityService.setDocumentId(documentId).setEntityId(entityId);
  }

}
