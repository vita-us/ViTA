package de.unistuttgart.vis.vita.analysis;


import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;

public class TestModuleRegistry {

  private static ModuleRegistry registry;

  @BeforeClass
  public static void initialize() {
    registry = new ModuleRegistry();
  }

  @Test
  public void testRegisterModule() {
    MockModule module = new MockModule();
    registry.registerModule(String.class, MockModule.class);

    assertThat(registry.getSize(), is(1));
    assertEquals(registry.getModuleClassFor(String.class), MockModule.class);
  }

}
