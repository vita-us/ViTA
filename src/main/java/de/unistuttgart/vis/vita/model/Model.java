package de.unistuttgart.vis.vita.model;

import de.unistuttgart.vis.vita.analysis.AnalysisResetter;
import de.unistuttgart.vis.vita.analysis.AnalysisStatus;
import de.unistuttgart.vis.vita.model.dao.DocumentDao;
import de.unistuttgart.vis.vita.model.document.Document;
import org.glassfish.hk2.api.Factory;
import org.glassfish.jersey.server.CloseableService;

import de.unistuttgart.vis.vita.model.dao.DaoFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Represents the Model of the application.
 */
@ManagedBean
@ApplicationScoped
public class Model implements Factory<EntityManager> {

  private static final String PERSISTENCE_UNIT_NAME = "de.unistuttgart.vis.vita";

  static {
    /*
     * Glassfish does not use the driver provided in the war if not explicitly loaded:
     * https://java.net/jira/browse/GLASSFISH-19451
     */
    loadDriver();
  }
  protected EntityManagerFactory entityManagerFactory;
  @Inject
  CloseableService closeableService;
  private TextRepository textRepository;
  private GateDatastoreLocation gateDatastoreLocation;

  /**
   * Create a default Model instance
   */
  public Model() {
    this(Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME), new TextRepository());
  }

  protected Model(EntityManagerFactory emf, TextRepository textRepository) {
    this.entityManagerFactory = emf;
    this.textRepository = textRepository;
    this.gateDatastoreLocation = new GateDatastoreLocation();
  }

  private static void loadDriver() {
    try {
      Class.forName("org.h2.Driver");
    } catch (ClassNotFoundException e) {
      Logger.getLogger(Model.class.getName()).log(Level.WARNING, "Unable to load H2 driver", e);
    }

    try {
      Class.forName("com.mysql.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      Logger.getLogger(Model.class.getName()).log(Level.WARNING, "Unable to load mysql driver", e);
    }
  }

  public GateDatastoreLocation getGateDatastoreLocation() {
    return gateDatastoreLocation;
  }

  protected void setGateDatastoreLocation(GateDatastoreLocation gateDatastoreLocation) {
    this.gateDatastoreLocation = gateDatastoreLocation;
  }

  /**
   * @return The entity manager.
   */
  public EntityManager getEntityManager() {
    return entityManagerFactory.createEntityManager();
  }

  /**
   * @return the TextRepository
   */
  public TextRepository getTextRepository() {
    return textRepository;
  }

  @Override
  @RequestScoped
  public EntityManager provide() {
    // hk2
    final EntityManager instance = getEntityManager();

    if (closeableService != null) {
      closeableService.add(new Closeable() {
        @Override
        public void close() throws IOException {
          dispose(instance);
        }
      });
    }
    return instance;
  }

  @RequestScoped
  @Produces
  public EntityManager produce() {
    // weld
    return getEntityManager();
  }

  @Override
  public void dispose(@Disposes EntityManager instance) {
    if (!instance.isOpen()) {
      return;
    }

    instance.close();
  }

  public void closeAllEntityManagers() {
    entityManagerFactory.close();
  }

  public void runInTransaction(TransactionalAction callback) {
    EntityManager em = getEntityManager();
    try {
      em.getTransaction().begin();
      callback.run(em, new DaoFactory(em));
      em.getTransaction().commit();
    } finally {
      em.close();
    }
  }
}
