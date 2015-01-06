package de.unistuttgart.vis.vita.model.dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.Person;

/**
 * Represents a generic data access object for entities.
 */
@ManagedBean
public class EntityDao extends JpaDao<Entity, String> {
  
  /**
   * Creates a new data access object for Entities.
   */
  public EntityDao() {
    super(Entity.class);
  }

  /**
   * @return the name of the {@link NamedQuery} for searching in a specific Document
   */
  public String getInDocumentQueryName() {
    String className = getPersistentClassName();
    return className + "." + "find" + className + "InDocument";
  }

  /**
   * Finds all Entities which occur in the document with the given id.
   * 
   * @param docId - the id of the document to search in
   * @param offset - the first place to be returned
   * @param count - the maximum of places to be returned
   * @return list of all Entities occurring in the given document
   */
  public List<Entity> findInDocument(String docId, int offset, int count) {
    TypedQuery<Entity> docQuery = em.createNamedQuery(getInDocumentQueryName(), Entity.class);
    docQuery.setParameter(0, docId);
    return docQuery.getResultList();
  }
  
  /**
   * @return the name of the {@link NamedQuery} for searching a specific name
   */
  public String getByNameQueryName() {
    String className = getPersistentClassName();
    return className + "." + "find" + className + "ByName";
  }

  /**
   * Finds the Entity with the given name.
   * 
   * @param name - the name of the Entity to search for
   * @return the Entity with the given name
   */
  public Entity findByName(String name) {
    TypedQuery<Entity> nameQuery = em.createNamedQuery(getByNameQueryName(), getPersistentClass());
    nameQuery.setParameter(0, name);
    return nameQuery.getSingleResult();
  }
  
  @SuppressWarnings("unchecked")
  public List<Entity> findOccurringPersons(int startOffset, int endOffset, List<Person> entities) {
    Query query = em.createNamedQuery("TextSpan.getOccurringEntities");
    query.setParameter("entities", entities);
    query.setParameter("rangeStart", startOffset);
    query.setParameter("rangeEnd", endOffset);
    return (List<Entity>)query.getResultList();
  }

}
