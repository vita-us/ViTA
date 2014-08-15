package de.unistuttgart.vis.vita.analysis;

import java.util.HashMap;
import java.util.Map;

/**
 * This class returns the result for a given module class.
 * TODO über die genaue Implementierung nochmal reden!!
 *
 */
public class ModuleResultProvider {
  
  private Map<Class<?>, Object> resultsModule;
  
  /**
   * New instance of the result provider.
   */
  public ModuleResultProvider() {
    resultsModule = new HashMap<>();
  }

  /**
   * Adds a available result to the provider.
   * @param theClass The class of the result.
   * @param result The result.
   */
  public void registerResult(Class<?> theClass, Object result) {
    if(theClass == null || result == null) {
      return;
    }

    resultsModule.put(theClass, result);
  }

  /**
   * Return the result of the module class.
   * 
   * @param module The desired module class.
   * @return The result.
   */
  public <T> T getResultFor(Class<T> module) {
    return (T) resultsModule.get(module);
  }

  public Class<Module<?>> getResultClassFor(Module<?> importModule) {

    return null;
  }
}
