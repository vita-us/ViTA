package de.unistuttgart.vis.vita.services;

import de.unistuttgart.vis.vita.analysis.AnalysisController;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.reflections.Reflections;

import javax.annotation.ManagedBean;
import javax.inject.Singleton;

/**
 * The base for hk2-based applications
 */
public class Hk2Application extends BaseApplication {
  public Hk2Application() {
    register(getBinder());
    register(ServiceLocatorUtilities.createAndPopulateServiceLocator());
  }

  /**
   * Gets the binder that will be used for dependency injection
   * @return the binder
   */
  protected AbstractBinder getBinder() {
    return new BaseApplicationBinder();
  }

  protected static class BaseApplicationBinder extends AbstractBinder {

    @Override
    protected void configure() {
      bindAllManagedBeans(SERVICES_PACKAGE);
      bindAllManagedBeans(DAO_PACKAGE);
      bindAllManagedBeans(ANALYSIS_PACKAGE);

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
