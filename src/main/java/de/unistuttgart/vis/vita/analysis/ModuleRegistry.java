package de.unistuttgart.vis.vita.analysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;

/**
 * A registry for saving all modules which should be executed on a document.
 *
 */
public class ModuleRegistry {
  private static final String MODULES_PACKAGE_NAME = "de.unistuttgart.vis.vita.analysis.modules";

  private static ModuleRegistry defaultRegistry;
  
  /**
   * Maps result classes to the providing module classes
   */
  private Map<Class<?>, ModuleClass> modulesForResultClasses;

  /**
   * Collects all contained module classes
   */
  private Set<ModuleClass> moduleClasses;


  /**
   * Make a new instance of the registry which is empty.
   */
  public ModuleRegistry() {
    // This should be thread-safe
    modulesForResultClasses = Collections.synchronizedMap(new HashMap<Class<?>, ModuleClass>());
    moduleClasses = Collections.synchronizedSet(new HashSet<ModuleClass>());
  }
  
  /**
   * Create a registry with the modules in the specified package
   * @param packageName the package with the modules
   */
  public ModuleRegistry(String packageName) {
    Iterable<Class<?>> moduleClasses =
        new Reflections(packageName).getTypesAnnotatedWith(AnalysisModule.class);
    for (Class<?> clazz : moduleClasses) {
      registerModule(clazz);
    }
  }

  /**
   * Returns the registry that contains all available modules
   * 
   * @return Empty registry.
   */
  public static ModuleRegistry getDefaultRegistry() {
    if (defaultRegistry != null) {
      defaultRegistry = new ModuleRegistry(MODULES_PACKAGE_NAME);
    }
    return defaultRegistry;
  }

  /**
   * Registers a new module to the registry.
   * 
   * @param moduleClass The module class which will be registered.
   */
  public void registerModule(Class<?> moduleClass) {
    registerModule(ModuleClass.get(moduleClass));
  }

  /**
   * Registers a new module to the registry.
   * 
   * @param moduleClass The module class which will be registered.
   */
  public void registerModule(ModuleClass moduleClass) {
    moduleClasses.add(moduleClass);
    modulesForResultClasses.put(moduleClass.getResultClass(), moduleClass);
  }

  /**
   * Returns the class of the module for given result class.
   * 
   * @param resultClass The result class to look for.
   * @return Class of the correct module if found else null.
   */
  public ModuleClass getModuleClassFor(Class<?> resultClass) {
    return modulesForResultClasses.get(resultClass);
  }

  /**
   * @return The number ofunmodifiableset thread safety registered modules.
   */
  public int getSize() {
    return moduleClasses.size();
  }

  /**
   * @return The registered modules in a list.
   */
  public Set<ModuleClass> getModules() {
    return Collections.unmodifiableSet(moduleClasses);
  }
}
