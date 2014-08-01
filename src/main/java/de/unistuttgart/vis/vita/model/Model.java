package de.unistuttgart.vis.vita.model;

import javax.persistence.EntityManager;

/**
 * Represents the Model of the application. 
 * 
 * @author Marc Weise
 * @version 0.1 01.08.2014
 */
public class Model {
  
  // attributes
  private TextRepository textRepo;
  
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
