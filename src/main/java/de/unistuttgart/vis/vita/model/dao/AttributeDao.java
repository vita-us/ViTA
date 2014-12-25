package de.unistuttgart.vis.vita.model.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.entity.Attribute;

/**
 * Represents a data access object for accessing Attributes.
 */
public class AttributeDao extends JpaDao<Attribute, String> {
  
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
   * @return a list of attributes of the given entity
   */
  public List<Attribute> findAttributesForEntity(String entityId) {
    TypedQuery<Attribute> entityQuery = em.createNamedQuery("Attribute.findAttributesForEntity",
                                                            Attribute.class);
    entityQuery.setParameter("entityId", entityId);
    return entityQuery.getResultList();
  }
  
  /**
   * Finds all Attributes of a given type.
   * 
   * @param attributeType - the type of attribute
   * @return a list of all Attributes with the given type
   */
  public List<Attribute> findAttributeForType(String attributeType) {
    TypedQuery<Attribute> typeQuery = em.createNamedQuery("Attribute.findAttributeByType",
                                                          Attribute.class);
    typeQuery.setParameter("attributeType", attributeType);
    return typeQuery.getResultList();
  }

}
