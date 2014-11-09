package de.unistuttgart.vis.vita;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.servlet.GrizzlyWebContainerFactory;

public class Main {
  private static URI getBaseURI() {
    return UriBuilder.fromUri("http://localhost/webapi").port(9998).build();
  }

  private static HttpServer startServer() throws IOException {
    HttpServer server =
        GrizzlyWebContainerFactory.create(getBaseURI(), Collections.singletonMap(
        "javax.ws.rs.Application",
        "de.unistuttgart.vis.vita.services.MainApplication"));

    server.getServerConfiguration().addHttpHandler(
        new CLStaticHttpHandler(Main.class.getClassLoader(), "/"));

    return server;
  }

  public static void main(String[] args) throws IOException {
    HttpServer server = startServer();
    System.out.println("server started at " + getBaseURI());
    System.in.read();
    server.shutdown();
  }
}
