/*
 * dummy.java
 *
 */

package de.unistuttgart.vis.vita.analysis;

import java.util.Collection;

/**
 * @author Vincent Link, Eduard Marbach
 */
public class MockModule implements IModule<String> {

  @Override
  public <T> Collection<Class<T>> getDependencies() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T> void observeProgress(Class<T> resultClass, double progress, boolean isReady) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public String execute(ModuleResultProvider result, IProgressListener progressListener) {
    // TODO Auto-generated method stub
    return null;
  }
}
