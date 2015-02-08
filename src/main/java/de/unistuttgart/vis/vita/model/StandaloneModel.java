package de.unistuttgart.vis.vita.model;

import javax.persistence.Persistence;

/**
 * Represents the Model to be used in the standalone application, which uses a h2 database on the
 * local file system.
 */
public class StandaloneModel extends Model {

  private static final String STANDALONE_PERSISTENCE_UNIT_NAME =
      "de.unistuttgart.vis.vita.standalone";

  /**
   * Creates a new StandaloneModel selecting the referring persistence unit and creating a new
   * TextRepository.
   */
  public StandaloneModel() {
    super(Persistence.createEntityManagerFactory(STANDALONE_PERSISTENCE_UNIT_NAME),
        new TextRepository(new DefaultDirectoryFactory()));
  }

}
