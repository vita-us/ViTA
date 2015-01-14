/*
 * GateDatastoreLocation.java
 *
 */

package de.unistuttgart.vis.vita.model;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 */
public class GateDatastoreLocation {

  private static final String VITA_DIR = ".vita";
  private static final String GATE_DS_DIR = "gate_datastore";
  private String home_dir = System.getProperty("user.home");
  private URI location;

  public GateDatastoreLocation() {
    Path gate_datastore = Paths.get(home_dir).resolve(VITA_DIR).resolve(GATE_DS_DIR);
    location = gate_datastore.toUri();
  }

  public GateDatastoreLocation(URI location) {
    this.location = location;
  }

  public GateDatastoreLocation(String subLocation) {
    Path gate_datastore = Paths.get(home_dir).resolve(VITA_DIR).resolve(subLocation);
    location = gate_datastore.toUri();
  }

  public URI getLocation() {
    return location;
  }

  public void setLocation(URI location) {
    this.location = location;
  }
}
