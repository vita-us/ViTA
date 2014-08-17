package de.unistuttgart.vis.vita.analysis;


import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestModuleRegistry {

  private static ModuleRegistry registry;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    registry = new ModuleRegistry();
  }

  @Test
  public void testRegisterModule() {
    registry.registerModule(MockModule.class);

    assertThat(registry.getSize(), is(1));
    assertEquals(registry.getModuleClassFor(String.class), MockModule.class);
  }

}
