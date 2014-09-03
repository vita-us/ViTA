package de.unistuttgart.vis.vita.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Traces dependencies of modules and prepares an execution
 */
public class AnalysisScheduler {
  /**
   * Lists all the modules that have to be executed, with the modules they depend on
   */
  private Map<ModuleClass, ModuleExecutionState> scheduledModules = new HashMap<>();

  /**
   * Keeps track of which modules have already been added
   */
  private Set<ModuleClass> scheduledModuleClasses = new HashSet<>();

  /**
   * Remembers which of the initial modules provides which result.
   * <p>
   * This is required because the ModuleRegistry may contain multiple modules for some results. All
   * module classes in this map should be instantiable or already instantiated.
   */
  private Map<Class<?>, ModuleClass> modulesForResultClass = new HashMap<>();
  
  private ModuleRegistry registry;

  /**
   * Schedules executing all modules required for the given target
   * @param targetModule the module that finishes the execution
   * @param initialModules pre-initialized modules that can be used
   */
  public AnalysisScheduler(ModuleRegistry registry, ModuleClass targetModule,
      Module<?>... initialModules) {
    this.registry = registry;
    
    for (Module<?> module : initialModules) {
      if (!scheduleModule(ModuleClass.get(module.getClass()), module)) {
        throw new IllegalArgumentException("For the module class " + module.getClass()
            + "there is more than one instance provided");
      }
    }

    scheduleModule(targetModule, null);
  }
  
  /**
   * Gets the modules that have to be executed
   * <p>
   * The result is not thread-safe.
   * @return the scheduled modules
   */
  public Collection<ModuleExecutionState> getScheduledModules() {
    return scheduledModules.values();
  }

  /**
   * Schedules the execution of a module and its dependencies
   * 
   * @param moduleClass the module class
   * @param optionalInstance an optional instance of the class, or null
   * @return true, if the module has been added, or false if the module class has already been added
   */
  private boolean scheduleModule(ModuleClass moduleClass, Module<?> optionalInstance) {
    if (scheduledModuleClasses.contains(moduleClass))
      return false;

    if (!moduleClass.canInstantiate() && optionalInstance == null) {
      throw new UnresolvedModuleDependencyException("The module " + moduleClass
          + "has no parameterless constructor and there is no instance provided for it.");
    }

    modulesForResultClass.put(moduleClass.getResultClass(), moduleClass);
    scheduledModuleClasses.add(moduleClass);
    Set<ModuleClass> dependencyModuleClasses = new HashSet<>();
    Set<ModuleClass> directAndIndirectDependencyModuleClasses = new HashSet<>();

    for (Class<?> dependencyResultClass : moduleClass.getDependencies()) {
      ModuleClass dependencyModuleClass = getModuleClassFor(dependencyResultClass, moduleClass);
      scheduleModule(dependencyModuleClass, null);
      dependencyModuleClasses.add(dependencyModuleClass);
      directAndIndirectDependencyModuleClasses.add(dependencyModuleClass);
      directAndIndirectDependencyModuleClasses
          .addAll(scheduledModules.get(dependencyModuleClass).getDirectAndIndirectDependencies());
    }

    scheduledModules.put(moduleClass, new ModuleExecutionState(moduleClass, optionalInstance,
                                                               dependencyModuleClasses,
                                                               directAndIndirectDependencyModuleClasses));
    return true;
  }

  /**
   * Gets the module that should be used when a specific result is required
   * @param resultClass the required result
   * @param dependentClass the module that requires this result (for better exceptions)
   * @return the module that provides the required result
   */
  private ModuleClass getModuleClassFor(Class<?> resultClass, ModuleClass dependentClass) {
    if (modulesForResultClass.containsKey(resultClass)) {
      return modulesForResultClass.get(resultClass);
    }
    
    ModuleClass clazz = registry.getModuleClassFor(resultClass);
    if (clazz == null) {
      throw new UnresolvedModuleDependencyException("The module " + dependentClass.getClass().getName() + 
          "depends on the result " + resultClass.getName() + ", but there is no module available for that.");
    }
    
    modulesForResultClass.put(resultClass, clazz);
    
    return clazz;
  }
}
