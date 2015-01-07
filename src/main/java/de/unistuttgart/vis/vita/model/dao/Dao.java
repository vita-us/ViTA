package de.unistuttgart.vis.vita.model.dao;

import java.io.Serializable;
import java.util.List;

/**
 * A generic Interface for a Data Access Object (DAO)
 * 
 * @param <T> the type of entity returned by the DAO
 * @param <I> the type of the index
 */
public interface Dao<T, I extends Serializable> {
  
  T findById(I id);
  
  List<T> findAll();
  
  void save(T entity);
  
  void update(T entity);
  
  void remove(T entity);
  
}
