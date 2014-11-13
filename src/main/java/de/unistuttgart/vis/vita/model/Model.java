package de.unistuttgart.vis.vita.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.glassfish.hk2.api.Factory;

import com.google.common.collect.ImmutableMap;

/**
 * Represents the Model of the application.
 */
@ApplicationScoped
public class Model implements Factory<EntityManager> {
  private TextRepository textRepository = new TextRepository();
  private EntityManagerFactory entityManagerFactory;
  
  private static final Logger LOGGER = Logger.getLogger(Model.class.getName());

  private static final String RELATIVE_DATA_DIRECTORY_ROOT = ".vita";
  private static final String PERSISTENCE_UNIT_NAME = "de.unistuttgart.vis.vita";
  private static final String UNITTEST_PERSISTENCE_UNIT_NAME =
      "de.unistuttgart.vis.vita.unittest.drop";
  private static final String UNITTEST_PERSISTENCE_UNIT_NAME_NODROP =
      "de.unistuttgart.vis.vita.unittest";

  static {
    /*
     * Glassfish does not use the driver provided in the war if not explicitly loaded:
     * https://java.net/jira/browse/GLASSFISH-19451
     */
    loadDriver();
  }

  private static Path getDefaultDataDirectory() {
    String appName;
    try {
      appName = new InitialContext().lookup("java:app/AppName").toString();
    } catch (NamingException e) {
      LOGGER.log(Level.INFO, "Unable to determine application name, using 'default'", e);
      appName = "default";
    }
    return Paths.get(System.getProperty("user.home")).resolve(RELATIVE_DATA_DIRECTORY_ROOT)
        .resolve(appName);
  }

  /**
   * Create a Model to be used in unit tests
   * <p>
   * The database will be automatically dropped and recreated for each call.
   * 
   * @return the model
   */
  public static Model createUnitTestModel() {
    return new Model(UNITTEST_PERSISTENCE_UNIT_NAME);
  }

  public static Model createUnitTestModelWithoutDrop() {
    return new Model(UNITTEST_PERSISTENCE_UNIT_NAME_NODROP);
  }

  /**
   * Create a default Model instance
   */
  public Model() {
    entityManagerFactory =
        Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
  }

  private Model(String persistenceUnitName) {
    entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
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

  @Override
  @RequestScoped
  @Produces
  public EntityManager provide() {
    return getEntityManager();
  }

  @Override
  public void dispose(EntityManager instance) {
    instance.close();
  }
}
