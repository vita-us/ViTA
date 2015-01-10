package de.unistuttgart.vis.vita.services;

import javax.annotation.ManagedBean;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.reflections.Reflections;

import de.unistuttgart.vis.vita.analysis.AnalysisController;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.StandaloneModel;


public class StandaloneApplication extends BaseApplication {

  public StandaloneApplication() {
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
