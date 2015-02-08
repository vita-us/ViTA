package de.unistuttgart.vis.vita.services;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.StandaloneEntityManagerFactory;
import de.unistuttgart.vis.vita.model.StandaloneModel;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;
import javax.persistence.EntityManager;

/**
 * The application config used in the Main class
 */
public class StandaloneApplication extends Hk2Application {
  @Override protected AbstractBinder getBinder() {
    return new MainApplicationBinder();
  }

  private static class MainApplicationBinder extends BaseApplicationBinder {

    @Override
    protected void configure() {
      super.configure();

      bind(StandaloneModel.class).in(Singleton.class).to(Model.class);
      bindFactory(StandaloneEntityManagerFactory.class).to(EntityManager.class);
    }
  }
}
