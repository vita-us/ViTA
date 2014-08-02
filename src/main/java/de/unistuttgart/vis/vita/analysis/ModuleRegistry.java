package de.unistuttgart.vis.vita.analysis;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ModuleRegistry {
  Map<Class, Class> registry;
  IModule module;

  public ModuleRegistry() {
    this.registry = new HashMap<>();
    this.module = module;
  }

  public static ModuleRegistry getDefaultRegistry() {

    return new ModuleRegistry();

  }

  public List<Class> getModules() {

    return null;

  }

  //
  public <TResult> void registerModule(Class<TResult> resultClass,
      Class<IModule<TResult>> moduleClass) {
    registry.put(resultClass, moduleClass);
  }

}
