package de.unistuttgart.vis.vita.analysis;

import java.lang.reflect.Type;
import java.util.Collection;

import net.jodah.typetools.TypeResolver;
import net.jodah.typetools.TypeResolver.Unknown;

import com.google.common.collect.ImmutableSet;

import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;

/**
 * Describes a class for an analysis module
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
    clazz = moduleClass;
    
    AnalysisModule annotation = moduleClass.getAnnotation(AnalysisModule.class);
    if (annotation == null) {
      throw new InvalidModuleException(String.format(
          "the class %s does not have the @AnalysisModule annotation.", moduleClass.getName()));
    }

    if (!Module.class.isAssignableFrom(moduleClass)) {
      throw new InvalidModuleException(String.format(
          "The class %s does not implement the Module interface.", moduleClass.getName()));
    }
    
    resultClass = getResultClass(moduleClass);
    if (resultClass == null) {
      throw new InvalidModuleException(String.format(
          "The class %s should specify the concrete type parameter for the Module interface.",
          moduleClass.getName()));
    }
    
    dependencies = ImmutableSet.copyOf(annotation.dependencies());
    
    if (dependencies.contains(resultClass)) {
      throw new InvalidModuleException(String.format("The class %s depends on its own result",
          moduleClass.getName()));
    }

    try {
      moduleClass.getConstructor(new Class[0]);
      hasZeroArgumentConstructor = true;
    } catch (NoSuchMethodException e) {
      hasZeroArgumentConstructor = false;
    }
  }
  
  private static Class<?> getResultClass(Class<?> moduleClass) {
    // Somewhere in the inheritance/interface tree, there is a reference to Module<SomeClass>.
    // This finds that type along with its type parameter.
    Type genericModuleType = TypeResolver.resolveGenericType(Module.class, moduleClass);
    
    // Extract the SomeClass. Note that type arguments to SomeClass will be dropped.
    Class<?>[] typeParameters = TypeResolver.resolveRawArguments(genericModuleType, moduleClass);
    
    if (typeParameters == null || typeParameters.length < 1 || typeParameters[0].equals(Unknown.class)) {
      return null;
    }
    
    return typeParameters[0];
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
      throw new UnsupportedOperationException("This module class can not be instanciated directly");
    }
    
    try {
      return (Module<?>) clazz.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new UnsupportedOperationException("Can not instanciate this module class", e);
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

  @Override
  public String toString() {
    return clazz.toString();
  }
}
