package de.unistuttgart.vis.vita.model;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Represents the Model of the application.
 */
public class Model {
  private TextRepository textRepo;
  private EntityManagerFactory entityManagerFactory;
  
  private static final String PERSISTENCE_UNIT_NAME = "de.unistuttgart.vis.vita";
  private static final String UNITTEST_PERSISTENCE_UNIT_NAME = "de.unistuttgart.vis.vita.unittest";

  /**
   * Create a Model to be used in unit tests
   * <p>
   * The database will be automatically dropped and recreated for each call.
   * @return the model
   */
  public static Model createUnitTestModel() {
    return new Model(UNITTEST_PERSISTENCE_UNIT_NAME);
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

}
