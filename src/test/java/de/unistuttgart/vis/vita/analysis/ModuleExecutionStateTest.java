package de.unistuttgart.vis.vita.analysis;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import de.unistuttgart.vis.vita.analysis.modules.IntProvidingModule;
import de.unistuttgart.vis.vita.analysis.modules.MockModule;

public class ModuleExecutionStateTest {
  ModuleClass targetModule;
  ModuleClass dependencyModule;
  ModuleExecutionState moduleState;

  @Before
  public void setUp() {
    targetModule = ModuleClass.get(MockModule.class);
    dependencyModule = ModuleClass.get(IntProvidingModule.class);
    moduleState = new ModuleExecutionState(targetModule, null, ImmutableSet.of(dependencyModule), null /* TODO */);
  }

  @Test
  public void testGetters() {
    assertThat(moduleState.isExecutable(), is(false));
    assertThat(moduleState.getInstance(), is(not(nullValue())));
    assertTrue(moduleState.getInstance() instanceof MockModule);
    assertThat((MockModule) moduleState.getInstance(), is(sameInstance((MockModule) moduleState.getInstance())));
    assertThat(moduleState.getModuleClass(), is(targetModule));
    assertThat(moduleState.getRemainingDependencies(), containsInAnyOrder(dependencyModule));
    assertThat(moduleState.getThread(), is(nullValue()));
  }

  @Test(expected = IllegalStateException.class)
  public void testGetResultsProviderFailsWhenNotExecutable() {
    moduleState.getResultProvider();
  }

  @Test(expected = IllegalStateException.class)
  public void testSetThreadFailsWhenNotExecutable() {
    moduleState.setThread(null);
  }

  @Test(expected = IllegalStateException.class)
  public void testStartExecutionFailsWhenNotExecutable() {
    moduleState.startExecution();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNotifyDependencyFinishedFailsWithWrongType() {
    moduleState.notifyDependencyFinished(dependencyModule, "string"); // should be Integer
  }

  @Test
  public void testProvidingResults() {
    moduleState.notifyDependencyFinished(dependencyModule, 123);

    assertThat(moduleState.isExecutable(), is(true));
    assertThat(moduleState.getRemainingDependencies(), is(empty()));
    assertThat(moduleState.getResultProvider().getResultFor(Integer.class), is(123));

    Thread thread = new Thread();
    moduleState.setThread(thread);
    assertThat(moduleState.getThread(), is(thread));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFetchingWrongResultThrows() {
    // should be gracefully ignored as it is not a dependency, and the result should not be stored
    moduleState.notifyDependencyFinished(targetModule, "test");
    moduleState.notifyDependencyFinished(dependencyModule, 123);

    moduleState.getResultProvider().getResultFor(String.class);
  }
}
