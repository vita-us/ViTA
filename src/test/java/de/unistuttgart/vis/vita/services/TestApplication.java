package de.unistuttgart.vis.vita.services;

import javax.persistence.EntityManager;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import de.unistuttgart.vis.vita.analysis.AnalysisController;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.UnitTestModel;

/**
 * The application config used for the unit tests
 */
public class TestApplication extends Hk2Application {
  @Override protected AbstractBinder getBinder() {
    return new MyApplicationBinder();
  }

  private static class MyApplicationBinder extends BaseApplicationBinder {
    @Override
    protected void configure() {
      super.configure();

      bind(UnitTestModel.class).to(Model.class);
      bindFactory(UnitTestModel.class).to(EntityManager.class);
      bind(AnalysisController.class).to(AnalysisController.class);
    }
  }
}
