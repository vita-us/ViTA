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
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.StandaloneModel;
import de.unistuttgart.vis.vita.services.document.DocumentsService;

public class StandaloneApplication extends ResourceConfig {
  
  private static final String SERVICES_PACKAGE = "de.unistuttgart.vis.vita.services";
  private static final String DAO_PACKAGE = "de.unistuttgart.vis.vita.model.dao";

  public StandaloneApplication() {
    super(MultiPartFeature.class, DocumentsService.class);
    packages(true, SERVICES_PACKAGE);
    register(new MainApplicationBinder());
    ServiceLocatorUtilities.createAndPopulateServiceLocator();
  }

  private static class MainApplicationBinder extends AbstractBinder {
    
    @Override
    protected void configure() {
      bindAllManagedBeans(SERVICES_PACKAGE);
      bindAllManagedBeans(DAO_PACKAGE);

      Model model = new StandaloneModel();
      bind(model).to(Model.class);
      bindFactory(model).to(EntityManager.class);
      bind(AnalysisController.class).in(Singleton.class).to(AnalysisController.class);
    }

    /**
     * Binds all classes annotated with ManagedBean in the given package.
     * 
     * @param packageName - the name of the package to search in
     */
    private void bindAllManagedBeans(String packageName) {
      Iterable<Class<?>> managedBeanClasses =
          new Reflections(packageName).getTypesAnnotatedWith(ManagedBean.class);
      
      for (Class<?> managedBeanClass : managedBeanClasses) {
        bind(managedBeanClass).to(managedBeanClass);
      }
    }

  }
}
