package de.unistuttgart.vis.vita.services;

import javax.annotation.ManagedBean;
import javax.persistence.EntityManager;

import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.reflections.Reflections;

import de.unistuttgart.vis.vita.analysis.AnalysisController;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.UnitTestModel;
import de.unistuttgart.vis.vita.services.document.DocumentsService;

public class TestApplication extends BaseApplication {
  public TestApplication() {
    register(new MyApplicationBinder());
    register(ServiceLocatorUtilities.createAndPopulateServiceLocator());
  }

  private static class MyApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
      Iterable<Class<?>> services =
          new Reflections(SERVICES_PACKAGE).getTypesAnnotatedWith(ManagedBean.class);
      for (Class<?> service : services) {
        bind(service).to(service);
      }

      bind(UnitTestModel.class).to(Model.class);
      bindFactory(UnitTestModel.class).to(EntityManager.class);
      bind(AnalysisController.class).to(AnalysisController.class);
    }
  }
}
