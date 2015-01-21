package de.unistuttgart.vis.vita.model;

import de.unistuttgart.vis.vita.model.dao.DaoFactory;

import javax.persistence.EntityManager;

public interface TransactionalAction {
  void run(EntityManager em, DaoFactory daoFactory);
}
