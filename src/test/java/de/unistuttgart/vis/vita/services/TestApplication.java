package de.unistuttgart.vis.vita.services;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import de.unistuttgart.vis.vita.model.Model;

public class TestApplication extends ResourceConfig {
  public TestApplication() {
    super(MultiPartFeature.class, DocumentsService.class);
    packages(true, "de.unistuttgart.vis.vita.services");
    register(new MyApplicationBinder());
  }

  public static class MyApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
      bind(Model.createUnitTestModelWithoutDrop()).to(Model.class);
    }
  }
}
