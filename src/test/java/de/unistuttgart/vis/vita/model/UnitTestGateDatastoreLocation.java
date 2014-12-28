/*
 * UnitTestGateDatastoreLocation.java
 *
 */

package de.unistuttgart.vis.vita.model;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 */
public class UnitTestGateDatastoreLocation extends GateDatastoreLocation {

  public static final String RELATIVE_DIRECTORY_PATH = ".vita/test/gate_ds";
  private static Path test_path;

  public UnitTestGateDatastoreLocation() {
    test_path = Paths.get(System.getProperty("user.home")).resolve(RELATIVE_DIRECTORY_PATH);
    setLocation(test_path.toUri());
  }

  public static Path getRootPath() {
    return Paths.get(System.getProperty("user.home")).resolve(RELATIVE_DIRECTORY_PATH);
  }
}
