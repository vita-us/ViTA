package de.unistuttgart.vis.vita.model.dao;

import java.io.Serializable;
import java.util.List;

/**
 * A generic Interface for a Data Access Object (DAO), providing basic CRUD functions for persisted
 * entities of a specific type.
 * 
 * @param <T> the type of entity returned by the DAO
 * @param <I> the type of the index
 */
public interface Dao<T, I extends Serializable> {

  /**
   * Finds an entity of the specific type, which has the given id and returns it.
   *
   * @param id - the id of the entity to search for
   * @return the entity with the given id
   */
  T findById(I id);

  /**
   * Finds all entities of the specific type and returns them.
   *
   * @return list of all entities of the specific type.
   */
  List<T> findAll();

  /**
   * Persists the given entity.
   *
   * @param entity - the entity to be saved
   */
  void save(T entity);

  /**
   * Updates the persisted entity with the given values.
   *
   * @param entity - the entity to be updated
   */
  void update(T entity);

  /**
   * Removes the persisted entity referring to the given one.
   *
   * @param entity - the entity to be removed
   */
  void remove(T entity);
  
}
