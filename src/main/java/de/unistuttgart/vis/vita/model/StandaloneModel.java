package de.unistuttgart.vis.vita.model;

import javax.persistence.Persistence;

/**
 * Uses a h2 database on the local file system
 */
public class StandaloneModel extends Model {
  public static final String STANDALONE_PERSISTENCE_UNIT_NAME =
      "de.unistuttgart.vis.vita.standalone";
  
  public StandaloneModel() {
    super(Persistence.createEntityManagerFactory(STANDALONE_PERSISTENCE_UNIT_NAME),
        new TextRepository(new DefaultDirectoryFactory()));
  }
}
