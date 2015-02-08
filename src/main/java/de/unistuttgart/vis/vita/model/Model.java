package de.unistuttgart.vis.vita.model;

import org.glassfish.hk2.api.Factory;
import org.glassfish.jersey.server.CloseableService;

import de.unistuttgart.vis.vita.model.dao.DaoFactory;

import java.io.Closeable;
import java.io.IOException;
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
 * Represents the Model of the application, implementing a factory for EntityManagers.
 */
@ManagedBean
@ApplicationScoped
public class Model implements Factory<EntityManager> {

  private static final String PERSISTENCE_UNIT_NAME = "de.unistuttgart.vis.vita";

  static {
    /*
     * GlassFish does not use the driver provided in the war if not explicitly loaded:
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

  /**
   * Creates a new Model using a given EntityManagerFactory and TextRepository.
   *
   * @param emf - the EntityManagerFactory to be used in this Model
   * @param textRepository - the TextRepository to be used for the texts in this Model
   */
  protected Model(EntityManagerFactory emf, TextRepository textRepository) {
    this.entityManagerFactory = emf;
    this.textRepository = textRepository;
    this.gateDatastoreLocation = new GateDatastoreLocation();
  }

  /**
   * Loads the database drivers.
   */
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

  /**
   * @return the location where GATE stores the information for this Model
   */
  public GateDatastoreLocation getGateDatastoreLocation() {
    return gateDatastoreLocation;
  }

  /**
   * Sets the location where GATE should store its data for this Model to the given one.
   *
   * @param gateDatastoreLocation - the data store location for GATE
   */
  protected void setGateDatastoreLocation(GateDatastoreLocation gateDatastoreLocation) {
    this.gateDatastoreLocation = gateDatastoreLocation;
  }

  /**
   * @return a new EntityManager from the factory
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

  /**
   * Provides an EntityManager for this Model.
   *
   * @return new EntityManager
   */
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

  /**
   * Produces a new EntityManager.
   *
   * @return new EntityManager
   */
  @RequestScoped
  @Produces
  public EntityManager produce() {
    // weld
    return getEntityManager();
  }

  /**
   * Closes the given EntityManager when it is disposed.
   *
   * @param instance - the instance of EntityManager to be disposed
   */
  @Override
  public void dispose(@Disposes EntityManager instance) {
    if (!instance.isOpen()) {
      return;
    }

    instance.close();
  }

  /**
   * Closes the EntityManagerFactory and all instances it holds.
   */
  public void closeAllEntityManagers() {
    entityManagerFactory.close();
  }

  /**
   * Runs the given action in a new transaction.
   *
   * @param callback - the TransactionalAction including the run() method to be performed in a
   *                 sTransaction
   */
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
