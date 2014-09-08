package de.unistuttgart.vis.vita.services;

import javax.persistence.EntityManager;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import de.unistuttgart.vis.vita.model.Model;

public class TestApplication extends ResourceConfig {
  public TestApplication() {
    register(new MyApplicationBinder());
    packages(true, "de.unistuttgart.vis.vita.services");
  }

  public static class MyApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
      bind(Model.createUnitTestModelWithoutDrop()).to(Model.class);
    }
  }
}
