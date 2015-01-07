package de.unistuttgart.vis.vita.model.dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.entity.Attribute;

/**
 * Represents a data access object for accessing Attributes.
 */
@ManagedBean
public class AttributeDao extends JpaDao<Attribute, String> {
  
  private static final String ATTRIBUTE_TYPE_PARAMETER = "attributeType";
  private static final String ENTITY_ID_PARAMETER = "entityId";

  /**
   * Creates a new data access object for accessing Attributes.
   */
  public AttributeDao() {
    super(Attribute.class);
  }

  /**
   * Finds all Attributes for an entity with a given id.
   * 
   * @param entityId - the id of the entity
   * @param offset - the first Attribute to be returned
   * @param count - the maximum amount of Attributes to be returned
   * @return a list of attributes of the given entity
   */
  public List<Attribute> findAttributesForEntity(String entityId, int offset, int count) {
    TypedQuery<Attribute> entityQuery = em.createNamedQuery("Attribute.findAttributesForEntity",
                                                            Attribute.class);
    entityQuery.setParameter(ENTITY_ID_PARAMETER, entityId);
    entityQuery.setFirstResult(offset);
    entityQuery.setMaxResults(count);
    return entityQuery.getResultList();
  }
  
  /**
   * Finds all Attributes of a given type.
   * 
   * @param attributeType - the type of attribute
   * @param offset - the first Attribute to be returned
   * @param count - the maximum amount of Attributes to be returned
   * @return a list of all Attributes with the given type
   */
  public List<Attribute> findAttributeForType(String attributeType, int offset, int count) {
    TypedQuery<Attribute> typeQuery = em.createNamedQuery("Attribute.findAttributeByType",
                                                          Attribute.class);
    typeQuery.setParameter(ATTRIBUTE_TYPE_PARAMETER, attributeType);
    typeQuery.setFirstResult(offset);
    typeQuery.setMaxResults(count);
    return typeQuery.getResultList();
  }

}
