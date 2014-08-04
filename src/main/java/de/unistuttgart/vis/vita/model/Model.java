package de.unistuttgart.vis.vita.model;

import javax.persistence.EntityManager;

/**
 * Represents the Model of the application.
 */
public class Model {
  
  // attributes
  private TextRepository textRepo;

  /**
   * @return The entity manager.
   */
  public EntityManager getEntityManager() {
    // TODO not implemented yet
    return null;
  }
  
  /**
   * @return the TextRepository
   */
  public TextRepository getTextRepository() {
    return textRepo;
  }

}
