package de.unistuttgart.vis.vita.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Represents the Model of the application.
 */
@RequestScoped
public class Model {
  private TextRepository textRepo;
  private EntityManagerFactory entityManagerFactory;
  
  private static final String PERSISTENCE_UNIT_NAME = "de.unistuttgart.vis.vita";
  private static final String UNITTEST_PERSISTENCE_UNIT_NAME = "de.unistuttgart.vis.vita.unittest.drop";
  private static final String UNITTEST_PERSISTENCE_UNIT_NAME_NODROP = "de.unistuttgart.vis.vita.unittest";

  static {
    /* 
     * Glassfish does not use the driver provided in the war if not explicitly loaded:
     * https://java.net/jira/browse/GLASSFISH-19451
     */
    loadDriver();
  }
  
  /**
   * Create a Model to be used in unit tests
   * <p>
   * The database will be automatically dropped and recreated for each call.
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
    this(PERSISTENCE_UNIT_NAME);
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
    return textRepo;
  }
  
  private static void loadDriver() {
    try {
      Class.forName("org.h2.Driver");
    } catch (ClassNotFoundException e) {
      Logger.getLogger(Model.class.getName()).log(Level.WARNING, "Unable to load H2 driver", e);
    }
  }
}
