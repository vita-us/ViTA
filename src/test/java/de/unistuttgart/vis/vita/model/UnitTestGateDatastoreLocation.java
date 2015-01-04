/*
 * UnitTestGateDatastoreLocation.java
 *
 */

package de.unistuttgart.vis.vita.model;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

/**
 *
 */
public class UnitTestGateDatastoreLocation extends GateDatastoreLocation {
  private static final String RELATIVE_DIRECTORY_PATH = ".vita/test/gate_ds";
  
  private Path path;

  public UnitTestGateDatastoreLocation() {
    path = Paths.get(System.getProperty("user.home")).resolve(RELATIVE_DIRECTORY_PATH)
        .resolve(UUID.randomUUID().toString());
    setLocation(path.toUri());
  }
  
  public void remove() {
    try {
      FileUtils.deleteDirectory(path.toFile());
    } catch (IOException e) {
      throw new RuntimeException("Error cleaning up gate datastore location", e);
    }
  }
}
