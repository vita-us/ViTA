package de.unistuttgart.vis.vita.services;

import javax.annotation.ManagedBean;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import de.unistuttgart.vis.vita.services.responses.VersionInfo;

/**
 * A service that reports the version of the REST API
 */
@Path("version")
@ManagedBean
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
