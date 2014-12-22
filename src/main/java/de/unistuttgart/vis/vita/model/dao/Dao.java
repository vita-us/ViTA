package de.unistuttgart.vis.vita.model.dao;

import java.io.Serializable;
import java.util.List;

/**
 * A generic Interface for a Data Access Object (DAO)
 * 
 * @param <T> the type of entity returned by the DAO
 * @param <ID> the type of the index
 */
public interface Dao<T, ID extends Serializable> {
  
  T findById(ID id);
  
  List<T> findAll();
  
  void save(T entity);
  
  void remove(T entity);
  
}
