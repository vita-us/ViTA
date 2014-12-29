package de.unistuttgart.vis.vita.model;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * A model that uses a h2 database that can be dropped at any time with {@link #startNewSession()}
 */
public class UnitTestModel extends Model {
  private static final String UNITTEST_PERSISTENCE_UNIT_DROP_NAME =
      "de.unistuttgart.vis.vita.unittest.drop";

  private static EntityManagerFactory emfCache;
  private static UnitTestDirectoryFactory directoryFactory;
  private static UnitTestGateDatastoreLocation gateDatastoreLocation;

  public UnitTestModel() {
    super(getEntityManagerFactory(), new TextRepository(getDirectoryFactory()));
    setGateDatastoreLocation(getTestGateDatastoreLocation());
  }

  private static EntityManagerFactory getEntityManagerFactory() {
    if (emfCache == null)
      emfCache = Persistence.createEntityManagerFactory(UNITTEST_PERSISTENCE_UNIT_DROP_NAME);
    return emfCache;
  }
  
  private static final DirectoryFactory getDirectoryFactory() {
    if (directoryFactory == null) {
      directoryFactory = new UnitTestDirectoryFactory();
    }
    return directoryFactory;
  }
  
  private static final UnitTestGateDatastoreLocation getTestGateDatastoreLocation() {
    if (gateDatastoreLocation == null) {
      gateDatastoreLocation = new UnitTestGateDatastoreLocation();
    }
    return gateDatastoreLocation;
  }

  /**
   * Drops the old database so that the next instance of {@link UnitTestModel} will work on a fresh
   * database
   */
  public static void startNewSession() {
    if (emfCache != null) {
      if (emfCache.isOpen())
        emfCache.close();
      emfCache = null;
    }
    
    if (directoryFactory != null) {
      directoryFactory.remove();
      directoryFactory = null;
    }
    if (gateDatastoreLocation != null) {
      gateDatastoreLocation.remove();
      gateDatastoreLocation = null;
    }
  }
}
