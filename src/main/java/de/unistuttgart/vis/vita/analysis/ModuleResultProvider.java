package de.unistuttgart.vis.vita.analysis;


/**
 * Provides the result of modules declared as dependencies
 */
public interface ModuleResultProvider {
  /**
   * Return the result of the module class.
   * 
   * @param resultClass The desired result class.
   * @return The result.
   */
  public <T> T getResultFor(Class<T> resultClass);
}
