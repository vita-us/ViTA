package de.unistuttgart.vis.vita.services;

import javax.annotation.ManagedBean;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.reflections.Reflections;

import de.unistuttgart.vis.vita.analysis.AnalysisController;
import de.unistuttgart.vis.vita.model.HerokuModel;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.StandaloneModel;
import de.unistuttgart.vis.vita.services.document.DocumentsService;

public class StandaloneApplication extends ResourceConfig {
  private static final String SERVICES_PACKAGE = "de.unistuttgart.vis.vita.services";

  public StandaloneApplication() {
    super(MultiPartFeature.class, DocumentsService.class);
    packages(true, SERVICES_PACKAGE);
    register(new MainApplicationBinder());
    ServiceLocatorUtilities.createAndPopulateServiceLocator();
  }

  private static class MainApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
      Iterable<Class<?>> services =
          new Reflections(SERVICES_PACKAGE).getTypesAnnotatedWith(ManagedBean.class);
      for (Class<?> service : services) {
        bind(service).to(service);
      }

      Model model;
      if (System.getenv("DATABASE_URL") != null) {
        model = new HerokuModel();
      } else {
        model = new StandaloneModel();
      }
      bind(model).to(Model.class);
      bindFactory(model).to(EntityManager.class);
      bind(AnalysisController.class).in(Singleton.class).to(AnalysisController.class);
    }
  }
}
