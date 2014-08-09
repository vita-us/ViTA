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

  public Model() {  
	  entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
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
