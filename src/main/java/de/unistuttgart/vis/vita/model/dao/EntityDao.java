package de.unistuttgart.vis.vita.model.dao;

import java.util.List;

import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.Person;

/**
 * Represents a generic data access object for entities.
 * 
 * @param <T> - the class of the objects to be accessed via this data access object
 */
public class EntityDao<T extends Entity> extends JpaDao<T, String> {

  /**
   * Creates a new data access object for accessing a given Entity class.
   * 
   * @param entityClass - the class extending Entity to be accessed by the new data access object
   */
  public EntityDao(Class<T> entityClass) {
    super(entityClass);
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
  public List<T> findInDocument(String docId, int offset, int count) {
    TypedQuery<T> docQuery = em.createNamedQuery(getInDocumentQueryName(), getPersistentClass());
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
  public T findByName(String name) {
    TypedQuery<T> nameQuery = em.createNamedQuery(getByNameQueryName(), getPersistentClass());
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
