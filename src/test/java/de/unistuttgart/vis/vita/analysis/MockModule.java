/*
 * dummy.java
 *
 */

package de.unistuttgart.vis.vita.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Vincent Link, Eduard Marbach
 */
public class MockModule implements Module<String> {
  
  private List<Class<String>> dependencies = new ArrayList<>();

  public MockModule() {
    dependencies.add(String.class);
  }
  
  @Override
  public Collection<Class<String>> getDependencies() {
    return dependencies;
  }

  @Override
  public <T> void observeProgress(Class<T> resultClass, double progress, boolean isReady) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public String execute(ModuleResultProvider result, ProgressListener progressListener) {
    // TODO Auto-generated method stub
    return null;
  }
}
