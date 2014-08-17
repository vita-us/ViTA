package de.unistuttgart.vis.vita.analysis;


/**
 * This class returns the result for a given module class.
 * TODO über die genaue Implementierung nochmal reden!!
 *
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
