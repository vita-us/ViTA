package de.unistuttgart.vis.vita.analysis;

import java.util.HashMap;
import java.util.Map;

/**
 * This class returns the result for a given module class.
 * TODO über die genaue Implementierung nochmal reden!!
 *
 */
public class ModuleResultProvider {
  
  private Map<Class<?>, ?> resultsModule;
  
  /**
   * New instance of the result provider.
   */
  public ModuleResultProvider() {
    resultsModule = new HashMap<>();
  }

  /**
   * Return the result of the module class.
   * 
   * @param module The desired module class.
   * @return The result.
   */
  private <T> T getResultFor(Class<T> module) {
    return (T) resultsModule.get(module);
  }
}
