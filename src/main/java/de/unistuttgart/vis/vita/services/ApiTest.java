package de.unistuttgart.vis.vita.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import de.unistuttgart.vis.vita.services.responses.VersionInfo;

@Path("get")
public class ApiTest {
  @Path("version")
  @GET
  public VersionInfo getApiAsJSON() {
    VersionInfo versionInfo = new VersionInfo();
    versionInfo.setApi("1.0-SNAPSHOT");
    return versionInfo;
  }
}
