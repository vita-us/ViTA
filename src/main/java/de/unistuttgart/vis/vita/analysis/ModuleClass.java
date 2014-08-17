package de.unistuttgart.vis.vita.analysis;

import java.util.Collection;

import net.jodah.typetools.TypeResolver;

import com.google.common.collect.ImmutableSet;

import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;

/**
 * Describes a class for an analyisis module
 */
public final class ModuleClass {
  private final Class<?> clazz;
  private final Collection<Class<?>> dependencies;
  private final Class<?> resultClass;
  private boolean hasZeroArgumentConstructor;

  /**
   * Creates the class description for a specific class
   * 
   * @param moduleClass the class
   */
  private ModuleClass(Class<?> moduleClass) {
    AnalysisModule annotation = moduleClass.getAnnotation(AnalysisModule.class);
    if (annotation == null) {
      throw new InvalidModuleException("The class " + moduleClass.getName() + " does not have the "
          + "@AnalysisModule annotation.");
    }

    if (!Module.class.isAssignableFrom(moduleClass)) {
      throw new InvalidModuleException("The class " + moduleClass.getName() + " does not implement the "
          + "Module interface.");
    }

    Class<?>[] typeArguments = TypeResolver.resolveRawArguments(Module.class, moduleClass);
    if (typeArguments.length < 1) {
      throw new InvalidModuleException("The class " + moduleClass.getName() + " should specify the "
          + "concrete type parameter for the Module interface.");
    }
    
    clazz = moduleClass;
    resultClass = typeArguments[0];
    dependencies = ImmutableSet.copyOf(annotation.dependencies());
    
    try {
      moduleClass.getConstructor(new Class[0]);
      hasZeroArgumentConstructor = true;
    } catch (NoSuchMethodException e) {
      hasZeroArgumentConstructor = false;
    }
  }
  
  public static ModuleClass get(Class<?> clazz) {
    return new ModuleClass(clazz);
  }

  /**
   * Gets the result classes this module depends on
   * @return an immutable set
   */
  public Collection<Class<?>> getDependencies() {
    return dependencies;
  }

  /**
   * Gets the result class this module generates
   * @return the class
   */
  public Class<?> getResultClass() {
    return resultClass;
  }
  
  /**
   * Indicates whether this module can be instantiated directly
   * @return true, if newInstance() works
   */
  public boolean canInstantiate() {
    return hasZeroArgumentConstructor;
  }

  /**
   * Creates an instance, if possible
   * @return the module instance
   * @throws IllegalStateException canInstanciate() is false
   */
  public Module<?> newInstance() {
    if (!hasZeroArgumentConstructor) {
      throw new IllegalStateException("This module class can not be instanciated directly");
    }
    
    try {
      return (Module<?>) clazz.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ModuleClass)) {
      return false;
    }
    ModuleClass other = (ModuleClass)obj;
    return other.clazz.equals(this.clazz);
  }
  
  @Override
  public int hashCode() {
    return clazz.hashCode();
  }
}
