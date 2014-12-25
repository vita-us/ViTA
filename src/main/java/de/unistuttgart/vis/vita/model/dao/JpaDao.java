package de.unistuttgart.vis.vita.model.dao;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 * Generic implementation of a data access object (DAO).
 * 
 * @param <T> the type of the entities to be accessed via this DAO
 * @param <ID> the type of the entity id field
 */
public abstract class JpaDao<T, ID extends Serializable> implements Dao<T, ID> {
  
  private final Class<T> persistentClass;
  
  @Inject
  protected EntityManager em;

  /**
   * Creates a new GenericHibernateDAO for the given class and session.
   * 
   * @param persClass - the class of the objects to be persisted
   */
  public JpaDao(Class<T> persClass) {
    this.persistentClass = persClass;
  }

  /**
   * @return the class of the persisted objects
   */
  public Class<T> getPersistentClass() {
    return persistentClass;
  }

  /**
   * @return the prefix for named queries
   */
  public String getPersistentClassName() {
    return persistentClass.getSimpleName();
  }

  /**
   * Finds an entity by its given id.
   * 
   * @param id - the id of the entity to find
   * @return the entity with the given id
   */
  @Override
  public T findById(ID id) {
    return em.find(getPersistentClass(), id);
  }

  /**
   * Finds all entities.
   * 
   * @return list of all entities
   */
  @Override
  public List<T> findAll() {
    TypedQuery<T> query = em.createQuery("SELECT * FROM " + getPersistentClass().getSimpleName(),
      getPersistentClass());
    
    return query.getResultList();
  }

  /**
   * Saves the given entity in the database.
   * 
   * @param entity - the entity to be saved
   */
  @Override
  public void save(T entity) {
    em.persist(entity);
  }

  /**
   * Removes a given entity from the database.
   * 
   * @param entity - the entity to be removed
   */
  @Override
  public void remove(T entity) {
    em.remove(entity);
  }

  protected int performNamedCountQuery(String queryName, Object... queryParams) {
    Query numberQuery = em.createNamedQuery(queryName);
    int pos = 0;
    for (Object parameter : queryParams) {
      numberQuery.setParameter(pos, parameter);
      pos++;
    }
    return (int) numberQuery.getSingleResult();
  }
  
}
