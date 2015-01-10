package de.unistuttgart.vis.vita.services;

import javax.persistence.EntityManager;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import de.unistuttgart.vis.vita.analysis.AnalysisController;
import de.unistuttgart.vis.vita.model.Model;

public class MainApplication extends BaseApplication {
  public MainApplication() {
    register(new MainApplicationBinder());
  }

  private static class MainApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
      bind(Model.class).to(Model.class);
      bindFactory(Model.class).to(EntityManager.class);
      bind(AnalysisController.class).to(AnalysisController.class);
    }
  }
}
