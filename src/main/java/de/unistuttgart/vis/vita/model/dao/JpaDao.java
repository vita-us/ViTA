package de.unistuttgart.vis.vita.model.dao;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

/**
 * Generic implementation of a data access object (DAO).
 * 
 * @param <T> the type of the entities to be accessed via this DAO
 * @param <I> the type of the entity id field
 */
public abstract class JpaDao<T, I extends Serializable> implements Dao<T, I> {
  
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
  public T findById(I id) {
    T result = em.find(getPersistentClass(), id);

    if (result == null) {
      throw new NoResultException("No entity of type '" + getPersistentClassName()
                                  + "' found with id '" + id + "'!");
    }
    
    return em.find(getPersistentClass(), id);
  }

  /**
   * Finds all entities.
   * 
   * @return list of all entities
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<T> findAll() {
    return em.createQuery("From " + getPersistentClassName()).getResultList();
  }

  /**
   * Saves the given entity in the database.
   * 
   * @param entity - the entity to be saved
   */
  @Override
  public void save(T entity) {
    em.getTransaction().begin();
    em.persist(entity);
    em.getTransaction().commit();
  }
  
  public void update(T entity) {
    em.getTransaction().begin();
    em.merge(entity);
    em.getTransaction().commit();
  }

  /**
   * Removes a given entity from the database.
   * 
   * @param entity - the entity to be removed
   */
  @Override
  public void remove(T entity) {
    em.getTransaction().begin();
    em.remove(entity);
    em.getTransaction().commit();
  }
  
}
