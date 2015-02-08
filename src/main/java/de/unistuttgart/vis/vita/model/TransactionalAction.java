package de.unistuttgart.vis.vita.model;

import de.unistuttgart.vis.vita.model.dao.DaoFactory;

import javax.persistence.EntityManager;

/**
 * An Interface for an action which should be performed in one transaction.
 */
public interface TransactionalAction {

  /**
   * The implementation of this method is performed transactional.
   *
   * @param em - the EntityManager to be used
   * @param daoFactory - the Factory for the data access object
   */
  void run(EntityManager em, DaoFactory daoFactory);
}
