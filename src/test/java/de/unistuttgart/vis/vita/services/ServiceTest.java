package de.unistuttgart.vis.vita.services;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;

import javax.ws.rs.ProcessingException;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly2.servlet.GrizzlyWebContainerFactory;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.spi.TestContainer;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;

/**
 * Configures the Jersey Test to use the GrizzlyWebContainer, overriding the method
 * TestContainerFactory.
 */
public class ServiceTest extends JerseyTest {

  @Override
  protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
    return new TestContainerFactory() {
      @Override
      public TestContainer create(final URI baseUri, final DeploymentContext deploymentContext)
          throws IllegalArgumentException {
        return new TestContainer() {
          private HttpServer server;

          @Override
          public ClientConfig getClientConfig() {
            return null;
          }

          @Override
          public URI getBaseUri() {
            return baseUri;
          }

          @Override
          public void start() {
            try {
              this.server =
                  GrizzlyWebContainerFactory.create(baseUri, Collections.singletonMap(
                      "javax.ws.rs.Application",
                      "de.unistuttgart.vis.vita.services.TestApplication"));
            } catch (ProcessingException e) {
              throw new TestContainerException(e);
            } catch (IOException e) {
              throw new TestContainerException(e);
            }
          }

          @Override
          public void stop() {
            this.server.stop();
          }
        };
      }
    };
  }
}
