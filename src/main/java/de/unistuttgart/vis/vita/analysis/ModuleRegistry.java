package de.unistuttgart.vis.vita.analysis;

public class ModuleRegistry {
  
  public static ModuleRegistry getDefaultRegistry() {
    ModuleRegistry reg1 = new ModuleRegistry();
    return null;

  }

  public <TResult> void registerModule(Class<TResult> resultClass,
      Class<IModule<TResult>> moduleClass) {

  }

}
