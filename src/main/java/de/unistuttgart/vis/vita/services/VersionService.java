package de.unistuttgart.vis.vita.services;

import de.unistuttgart.vis.vita.services.responses.VersionInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * A service that reports the version of the REST API
 */
@Path("version")
public class VersionService {

  /**
   * Get the version of the REST API
   *
   * @return the version information
   */
  @GET
  public VersionInfo getApiAsJSON() {
    VersionInfo versionInfo = new VersionInfo();
    versionInfo.setApi("1.0-SNAPSHOT");
    return versionInfo;
  }
}
