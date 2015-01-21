package de.unistuttgart.vis.vita.services.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.dao.AttributeDao;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.services.BaseService;
import de.unistuttgart.vis.vita.services.responses.AttributesResponse;
import de.unistuttgart.vis.vita.services.responses.BasicAttribute;

/**
 * Provides a method to GET all attributes mentioned in the document this service refers to.
 */
@ManagedBean
public class AttributesService extends BaseService {

  private String documentId;
  private String entityId;

  private AttributeDao attributeDao;

  @Inject
  private AttributeService attributeService;

  @Override public void postConstruct() {
    super.postConstruct();
    attributeDao = getDaoFactory().getAttributeDao();
  }

  /**
   * Sets the id of the document this service refers to and returns itself.
   * 
   * @param docId - the id of the document for which this service offers Attributes of the current
   *        Entity
   * @return this AttributesService
   */
  public AttributesService setDocumentId(String docId) {
    this.documentId = docId;
    return this;
  }

  /**
   * Sets the id of the Entity this service refers to
   * 
   * @param id - the id of the Entity whose attributes this AttributesService should offer
   * @return this AttributesService
   */
  public AttributesService setEntityId(String id) {
    this.entityId = id;
    return this;
  }

  /**
   * Reads the attributes of the current Entity from the database and returns them in JSON.
   * 
   * @return a AttributesResponse including all Attributes of the current Entity
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public AttributesResponse getAttributes(@QueryParam("offset") int offset,
                                          @QueryParam("count") int count) {
    List<Attribute> attributes = attributeDao.findAttributesForEntity(entityId, offset, count);
    
    List<BasicAttribute> basicAttributes = convertToBasicAttribute(attributes);
    
    return new AttributesResponse(basicAttributes);
  }

  private List<BasicAttribute> convertToBasicAttribute(List<Attribute> attributes) {
    List<BasicAttribute> basicAttributes = new ArrayList<>();
    for (Attribute attribute: attributes) {
      basicAttributes.add(attribute.toBasicAttribute());
    }
    return basicAttributes;
  }

  /**
   * Returns the Service to access the Attribute with the given id.
   * 
   * @param id - the id of the Attribute to be accessed
   * @return the AttributeService to access the Attribute with the given id
   */
  @Path("{attributeId}")
  public AttributeService getAttribute(@PathParam("attributeId") String id) {
    return attributeService
        .setDocumentId(documentId)
        .setEntityId(entityId)
        .setAttributeId(id);
  }

}
