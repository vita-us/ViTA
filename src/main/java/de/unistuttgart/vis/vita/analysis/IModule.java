package de.unistuttgart.vis.vita.analysis;

import java.util.Collection;

public interface IModule<TResult> {
  public <T> Collection<Class<T>> getDependencies();

  public <T> void observeProgress(Class<T> resultClass, double progress, boolean isReady);

  public TResult execute(ModuleResultProvider result, IProgressListener progressListener);
}
