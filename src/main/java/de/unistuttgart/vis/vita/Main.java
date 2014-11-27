package de.unistuttgart.vis.vita;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.StringUtils;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.servlet.GrizzlyWebContainerFactory;

public class Main {
  private static final int DEFAULT_PORT = 9998;

  private static int getPort() {
    String portEnv = System.getenv("PORT");
    if (StringUtils.isEmpty(portEnv)) {
      return DEFAULT_PORT;
    }

    try {
      return Integer.parseInt(portEnv);
    } catch (NumberFormatException e) {
      throw new RuntimeException("Invalid PORT environment variable: " + portEnv, e);
    }
  }

  private static URI getBaseURI() {
    return UriBuilder.fromUri("http://localhost/webapi").port(getPort()).build();
  }

  private static HttpServer startServer() throws IOException {
    HttpServer server =
        GrizzlyWebContainerFactory.create(getBaseURI(), Collections.singletonMap(
            "javax.ws.rs.Application", "de.unistuttgart.vis.vita.services.StandaloneApplication"));

    // Use statics from classpath if e.g. in runnable jar, but use directory on disk
    // if run from IDE.
    Path webappPath = Paths.get(System.getProperty("user.dir"))
        .resolve("src").resolve("main").resolve("webapp");
    HttpHandler staticHandler;
    if (Files.isDirectory(webappPath))
      staticHandler = new StaticHttpHandler(webappPath.toString());
    else
      staticHandler = new CLStaticHttpHandler(Main.class.getClassLoader(), "/");
    server.getServerConfiguration().addHttpHandler(staticHandler);

    return server;
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    final HttpServer server = startServer();
    System.out.println("server started at http://localhost:" + getPort());

    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        @Override
        public void run() {
            System.out.println("Stopping server..");
            server.shutdown();
        }
    }, "shutdownHook"));

    Thread.currentThread().join();
  }
}
