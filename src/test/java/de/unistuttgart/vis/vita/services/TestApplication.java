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

public class TestApplication extends ResourceConfig {
  
  private static String SERVICES_PACKAGE = "de.unistuttgart.vis.vita.services";
  private static String DAO_PACKAGE = "de.unistuttgart.vis.vita.model.dao";
  private static String ANALYSIS_PACKAGE = "de.unistuttgart.vis.vita.analysis";

  public TestApplication() {
    super(MultiPartFeature.class, DocumentsService.class);
    packages(true, DAO_PACKAGE);
    packages(true, SERVICES_PACKAGE);
    packages(true, ANALYSIS_PACKAGE);
    register(new MyApplicationBinder());
    register(ServiceLocatorUtilities.createAndPopulateServiceLocator());
  }

  private static class MyApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
      bindAllManagedBeans(SERVICES_PACKAGE);
      bindAllManagedBeans(DAO_PACKAGE);   
      bindAllManagedBeans(ANALYSIS_PACKAGE);

      bind(UnitTestModel.class).to(Model.class);
      bindFactory(UnitTestModel.class).to(EntityManager.class);
      bind(AnalysisController.class).to(AnalysisController.class);
    }
    
    private void bindAllManagedBeans(String packageName) {
      Iterable<Class<?>> managedBeanClasses =
          new Reflections(packageName).getTypesAnnotatedWith(ManagedBean.class);
      
      for (Class<?> managedBeanClass : managedBeanClasses) {
        bind(managedBeanClass).to(managedBeanClass);
      }
    }
    
  }
}
