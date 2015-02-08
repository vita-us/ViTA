/*
 * GateDatastoreLocation.java
 *
 */

package de.unistuttgart.vis.vita.model;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Represents the location where GATE stores its data, holding its URI.
 */
public class GateDatastoreLocation {

  private static final String VITA_DIR = ".vita";
  private static final String GATE_DS_DIR = "gate_datastore";

  private String homeDir = System.getProperty("user.home");
  private URI location;

  /**
   * Creates a new instance of GateDatastoreLocation, holding to the default directory.
   */
  public GateDatastoreLocation() {
    Path gateDatastore = Paths.get(homeDir).resolve(VITA_DIR).resolve(GATE_DS_DIR);
    location = gateDatastore.toUri();
  }

  /**
   * Creates a new instance of GateDatastoreLocation, holding the given URI.
   *
   * @param location - the URI where GATE should store its data
   */
  public GateDatastoreLocation(URI location) {
    this.location = location;
  }

  public GateDatastoreLocation(String subLocation) {
    Path gateDatastore = Paths.get(homeDir).resolve(VITA_DIR).resolve(subLocation);
    location = gateDatastore.toUri();
  }

  /**
   * @return the location as URI
   */
  public URI getLocation() {
    return location;
  }

  /**
   * Sets the location to the given URI.
   *
   * @param location - the URI where GATE should store its data
   */
  public void setLocation(URI location) {
    this.location = location;
  }
}
