package de.unistuttgart.vis.vita.services;

import de.unistuttgart.vis.vita.services.responses.VersionInfo;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.core.Application;

import static org.junit.Assert.assertEquals;

/**
 * Test the Version service
 */
public class VersionServiceTest extends JerseyTest {

  @Override
  protected Application configure() {
    return new ResourceConfig(VersionService.class);
  }

  /**
   * Test if the API version is correct
   */
  @Test
  public void testAPIVersion() {
    VersionInfo versionInfoActual = target("version").request().get(VersionInfo.class);

    assertEquals("1.0-SNAPSHOT", versionInfoActual.getApi());
  }
}
