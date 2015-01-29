package de.unistuttgart.vis.vita.services;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.dao.DaoFactory;
import org.glassfish.hk2.api.PostConstruct;
import org.glassfish.jersey.server.CloseableService;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.Closeable;
import java.io.IOException;

public class BaseService implements PostConstruct, org.glassfish.hk2.api.PreDestroy,
    Closeable {
  @Inject private Model model;
  @Inject private CloseableService closeableService;
  @Inject private EntityManager em;

  private DaoFactory daoFactory;

  @javax.annotation.PostConstruct
  public void postConstructCaller() {
    postConstruct();
  }

  @Override
   public void postConstruct() {
    if (!em.getTransaction().isActive()) {
      em.getTransaction().begin();
    }
    daoFactory = new DaoFactory(em);
    closeableService.add(this);
  }

  @Override
  @PreDestroy public void preDestroy() {
    if (em != null) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().commit();
      }
    }
  }

  public Model getModel() {
    return model;
  }

  public DaoFactory getDaoFactory() {
    return daoFactory;
  }

  @Override public void close() throws IOException {
    preDestroy();
  }

  protected EntityManager getEntityManager() {
    return em;
  }
}
