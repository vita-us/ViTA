package de.unistuttgart.vis.vita.analysis;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import de.unistuttgart.vis.vita.analysis.mockmodules.IntProvidingModule;
import de.unistuttgart.vis.vita.analysis.mockmodules.MockModule;

public class ModuleExecutionStateTest {
  private ModuleClass targetModule;
  private ModuleClass dependencyModule;
  private ModuleExecutionState moduleState;
  private MockModule targetInstance;

  private static final double EPSILON = 0.001;

  @Before
  public void setUp() {
    targetModule = ModuleClass.get(MockModule.class);
    dependencyModule = ModuleClass.get(IntProvidingModule.class);
    moduleState = new ModuleExecutionState(targetModule, null, ImmutableSet.of(dependencyModule),
        ImmutableSet.of(dependencyModule));
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
    moduleState.notifyModuleFinished(dependencyModule, "string"); // should be Integer
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNotifyModuleProgressWithInvalidProgressThrows() {
    moduleState.notifyModuleProgress(dependencyModule, 2); // should be between 0 and 1
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNotifyModuleProgressWithInvalidProgressThrows2() {
    moduleState.notifyModuleProgress(dependencyModule, -0.1); // should be between 0 and 1
  }
  
  @Test
  public void testToStringContainsDependencies() {
    assertThat(moduleState.toString(), containsString("MockModule"));
    assertThat(moduleState.toString(), containsString("IntProvidingModule"));
  }

  @Test
  public void testProvidingResults() {
    moduleState.notifyModuleFinished(dependencyModule, 123);

    assertThat(moduleState.isExecutable(), is(true));
    assertThat(moduleState.getRemainingDependencies(), is(empty()));
    assertThat(moduleState.getResultProvider().getResultFor(Integer.class), is(123));

    Thread thread = new Thread();
    moduleState.setThread(thread);
    assertThat(moduleState.getThread(), is(thread));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testProvidingWrongResultThrows() {
    // depdendencyModule is of type Integer, so "abc" should not be accepted
    moduleState.notifyModuleFinished(dependencyModule, "abc");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFetchingWrongResultThrows() {
    // should be gracefully ignored as it is not a dependency, and the result should not be stored
    moduleState.notifyModuleFinished(targetModule, "test");
    moduleState.notifyModuleFinished(dependencyModule, 123);

    moduleState.getResultProvider().getResultFor(String.class);
  }

  @Test
  public void testReportsProgressWithNotifyDependencyProgressForDirectDependency() {
    setUpWithMockedTargetInstance();
    
    moduleState.notifyModuleProgress(dependencyModule, 0.5);
    
    // There are two modules to be executed, and one is halfway through, so it's one fourth
    // note that there should be not rounding issues with base-2 fractions
    verify(targetInstance).observeProgress(doubleThat(is(closeTo(0.25, EPSILON))));
  }

  @Test
  public void testReportsProgressWithNotifyDependencyFinishedForDirectDependency() {
    setUpWithMockedTargetInstance();
    
    moduleState.notifyModuleFinished(dependencyModule, 123);
    
    // There are two modules to be executed, and one is done
    verify(targetInstance).observeProgress(doubleThat(is(closeTo(0.5, EPSILON))));
  }

  @Test
  public void testReportsProgressWithNotifyDependencyProgressForModuleItself() {
    setUpWithMockedTargetInstance();
    moduleState.notifyModuleFinished(dependencyModule, 123);
    reset(targetInstance);
    
    moduleState.notifyModuleProgress(targetModule, 0.5);
    
    // One module is finished completely, and the other halfway through
    verify(targetInstance).observeProgress(doubleThat(is(closeTo(0.75, EPSILON))));
  }

  @Test
  public void testReportsProgressWithNotifyDependencyFinishedForModuleItself() {
    setUpWithMockedTargetInstance();
    moduleState.notifyModuleFinished(dependencyModule, 123);
    reset(targetInstance);
    
    moduleState.notifyModuleFinished(targetModule, "abc");
    
    // All dependencies and the target module are finished
    verify(targetInstance).observeProgress(doubleThat(is(closeTo(1.0, EPSILON))));
  }

  @Test
  public void testCallsDependencyFinished() {
    setUpWithMockedTargetInstance();

    moduleState.notifyModuleFinished(dependencyModule, IntProvidingModule.RESULT);

    verify(targetInstance).dependencyFinished(Integer.class, IntProvidingModule.RESULT);
  }

  @Test
  public void testCallsDependencyFailed() {
    setUpWithMockedTargetInstance();

    moduleState.notifyModuleFailed(dependencyModule);

    verify(targetInstance).dependencyFailed(Integer.class);
  }
  
  private void setUpWithMockedTargetInstance() {
    targetInstance = mock(MockModule.class);
    moduleState = new ModuleExecutionState(targetModule, targetInstance, ImmutableSet.of(dependencyModule),
        ImmutableSet.of(dependencyModule));
  }
}
