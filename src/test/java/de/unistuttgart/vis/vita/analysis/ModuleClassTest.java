package de.unistuttgart.vis.vita.analysis;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;

import de.unistuttgart.vis.vita.analysis.invalidmodules.ModuleDependentOnSelf;
import de.unistuttgart.vis.vita.analysis.invalidmodules.ModuleWithoutAnnotation;
import de.unistuttgart.vis.vita.analysis.invalidmodules.ModuleWithoutInterfaceImplementation;
import de.unistuttgart.vis.vita.analysis.invalidmodules.ModuleWithoutResultClass;
import de.unistuttgart.vis.vita.analysis.mockmodules.ManualModule;
import de.unistuttgart.vis.vita.analysis.mockmodules.MockModule;

public class ModuleClassTest {

  @Test
  public void testGetters() {
    ModuleClass clazz = ModuleClass.get(MockModule.class);
    assertEquals(String.class, clazz.getResultClass());
    assertThat(clazz.getDependencies(), 
        IsIterableContainingInOrder.<Class<?>>contains(Integer.class));
    assertTrue(clazz.canInstantiate());
  }
  
  @Test
  public void testUninstantiableModule() {
    ModuleClass clazz = ModuleClass.get(ManualModule.class);
    assertFalse(clazz.canInstantiate());
  }
  
  @Test
  public void testNewInstance() {
    ModuleClass clazz = ModuleClass.get(MockModule.class);
    Object instance = clazz.newInstance();
    assertNotNull(instance);
    assertThat(instance, is(instanceOf(MockModule.class)));
    
    Object instance2 = clazz.newInstance();
    assertThat(instance2, is(not(sameInstance(instance))));
  }
  
  @Test(expected=UnsupportedOperationException.class)
  public void testInstancintModuleWithoutZeroArgumentConstructorThrows() {
    ModuleClass clazz = ModuleClass.get(ManualModule.class);
    clazz.newInstance();
  }
  
  @Test(expected=InvalidModuleException.class)
  public void testRejectsModuleWithoutResultClass() {
    ModuleClass.get(ModuleWithoutResultClass.class);
  }
  
  @Test(expected=InvalidModuleException.class)
  public void testRejectsModuleThatDoesNotImplementInterface() {
    ModuleClass.get(ModuleWithoutInterfaceImplementation.class);
  }
  
  @Test(expected=InvalidModuleException.class)
  public void testRejectsModuleWithoutAnnotation() {
    ModuleClass.get(ModuleWithoutAnnotation.class);
  }

  @Test(expected = InvalidModuleException.class)
  public void testRejectsModuleDependingOnOwnResult() {
    ModuleClass.get(ModuleDependentOnSelf.class);
  }

}
