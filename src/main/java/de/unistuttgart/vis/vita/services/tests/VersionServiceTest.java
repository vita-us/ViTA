package de.unistuttgart.vis.vita.services.tests;


import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import de.unistuttgart.vis.vita.services.VersionService;
import de.unistuttgart.vis.vita.services.responses.VersionInfo;


public class VersionServiceTest extends JerseyTest {
  @Override
  protected Application configure() {
    return new ResourceConfig(VersionService.class);
  }

  @Test
  public void test() {
    VersionInfo versionInfoActual = target("version").request().get(VersionInfo.class);
    VersionInfo versionInfoExpected = new VersionInfo();
    versionInfoExpected.setApi("1.0-SNAPSHOT");
    assertEquals(versionInfoExpected.getApi(), versionInfoActual.getApi());
  }
}
