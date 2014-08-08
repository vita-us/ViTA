package de.unistuttgart.vis.vita.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A registry for saving all modules which should be executed on a document.
 *
 */
public class ModuleRegistry {

  private Map<Class<?>, Class<? extends Module<?>>> registry;

  /**
   * Make a new instance of the registry which is empty.
   */
  public ModuleRegistry() {
    registry = new HashMap<>();

  }

  /**
   * Returns the default registry which is an empty one.
   * 
   * @return Empty registry.
   */
  public static ModuleRegistry getDefaultRegistry() {
    return new ModuleRegistry();
  }

  /**
   * Registers a new module to the registry.
   * 
   * @param resultClass The class type of the module.
   * @param moduleClass The module class which will be registered.
   */
  public <TResult, TModule extends Module<TResult>> void registerModule(
      Class<TResult> resultClass, Class<TModule> moduleClass) {
    registry.put(resultClass, moduleClass);
  }

  /**
   * Returns the class of the module for given result class.
   * 
   * @param resultClass The result class to look for.
   * @return Class of the correct module if found else null.
   */
  public <TResult, TModule extends Module<TResult>> Class<TModule> getModuleClassFor(
      Class<TResult> resultClass) {
    if (registry.get(resultClass) != null) {
      return (Class<TModule>) registry.get(resultClass);
    } else {
      return null;
    }
  }

  /**
   * @return The number of registered modules.
   */
  public int getSize() {
    return registry.size();
  }

  /**
   * @return The registered modules in a list.
   */
  public List<Class<? extends Module<?>>> getModules() {
    // TODO
    return null;

  }

}
