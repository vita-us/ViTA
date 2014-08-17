package de.unistuttgart.vis.vita.analysis;


import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.hamcrest.core.IsSame;
import org.junit.Test;

import de.unistuttgart.vis.vita.analysis.modules.MockModule;

public class TestModuleRegistry {
  @Test
  public void testRegisterModule() {
    ModuleRegistry registry = new ModuleRegistry();
    registry.registerModule(MockModule.class);

    assertThat(registry.getSize(), is(1));
    assertEquals(registry.getModuleClassFor(String.class), ModuleClass.get(MockModule.class));
  }

  @Test
  public void testRegistryForPackage() {
    ModuleRegistry registry = new ModuleRegistry(MockModule.class.getPackage().getName());
    assertThat(registry.getModules(), hasItem(ModuleClass.get(MockModule.class)));
  }
  
  @Test
  public void testDefaultRegistry() {
    ModuleRegistry registry = ModuleRegistry.getDefaultRegistry();
    assertNotNull(registry);
    assertThat(ModuleRegistry.getDefaultRegistry(), IsSame.sameInstance(registry));
  }
}
