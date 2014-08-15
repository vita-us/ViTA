/*
 * ImportModule.java
 *
 */

package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;

import java.nio.file.Path;
import java.util.Collection;

/**
 * @author Vincent Link, Eduard Marbach
 */
public class ImportModule implements Module {

  private Path filePath;

  public ImportModule(Path filePath) {
    this.filePath = filePath;
  }

  @Override
  public Collection<Class<?>> getDependencies() {
    return null;
  }

  @Override
  public Object execute(ModuleResultProvider result, ProgressListener progressListener) {
    return null;
  }

  @Override
  public void observeProgress(Class resultClass, double progress, boolean isReady) {

  }
}
