package de.unistuttgart.vis.vita.analysis;


import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.analysis.modules.MockModule;

public class TestModuleRegistry {

  private ModuleRegistry registry;

  @Before
  public void setUp() throws Exception {
    registry = new ModuleRegistry();
  }

  @Test
  public void testRegisterModule() {
    registry.registerModule(MockModule.class);

    assertThat(registry.getSize(), is(1));
    assertEquals(registry.getModuleClassFor(String.class), MockModule.class);
  }

  @Test
  public void testRegistryForPackage() {
    registry = new ModuleRegistry(MockModule.class.getPackage().getName());
    assertThat(registry.getModules(), hasItem(ModuleClass.get(MockModule.class)));
  }
}
