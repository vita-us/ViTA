package de.unistuttgart.vis.vita.services.tests;


import static org.junit.Assert.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import de.unistuttgart.vis.vita.services.responses.VersionInfo;


public class VersionServiceTest extends JerseyTest {

  @Path("versiontest")
  public static class VersionResource {
    @GET
    public VersionInfo getApiAsJSON() {
      VersionInfo versionInfo = new VersionInfo();
      versionInfo.setApi("1.0-SNAPSHOT");
      return versionInfo;

    }
  }

  @Override
  protected Application configure() {
    return new ResourceConfig(VersionResource.class);
  }

  @Test
  public void test() {
    VersionInfo versionInfoActual = target("versiontest").request().get(VersionInfo.class);
    // System.out.println(target("versiontest").request().get().getStatusInfo().toString());
    VersionInfo versionInfoExpected = new VersionInfo();
    versionInfoExpected.setApi("1.0-SNAPSHOT");
    assertEquals(versionInfoExpected.getApi(), versionInfoActual.getApi());
  }
}
