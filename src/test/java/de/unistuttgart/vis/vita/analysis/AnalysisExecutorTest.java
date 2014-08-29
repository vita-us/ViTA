package de.unistuttgart.vis.vita.analysis;

import static com.jayway.awaitility.Awaitility.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import de.unistuttgart.vis.vita.analysis.modules.DebugBaseModule;
import de.unistuttgart.vis.vita.analysis.modules.IntProvidingModule;
import de.unistuttgart.vis.vita.analysis.modules.MockModule;

public class AnalysisExecutorTest {
  ModuleClass targetModule;
  ModuleClass dependencyModule;
  ModuleExecutionState targetModuleState;
  MockModule targetModuleInstance;
  IntProvidingModule dependencyModuleInstance;
  ModuleExecutionState dependencyModuleState;
  AnalysisExecutor executor;
  
  @Before
  public void setUp() {
    targetModule = ModuleClass.get(MockModule.class);
    targetModuleInstance = new MockModule();
    dependencyModule = ModuleClass.get(IntProvidingModule.class);
    dependencyModuleInstance = new IntProvidingModule();
    targetModuleState =
        new ModuleExecutionState(targetModule, targetModuleInstance,
            ImmutableSet.of(dependencyModule));
    Set<ModuleClass> empty = Collections.emptySet();
    dependencyModuleState =
        new ModuleExecutionState(dependencyModule, dependencyModuleInstance, empty);
    executor = new AnalysisExecutor(Arrays.asList(targetModuleState, dependencyModuleState));
  }

  @Test
  public void testRunning() {
    assertThat(executor.getStatus(), is(AnalysisStatus.NOT_STARTED));
    
    executor.start();
    assertThat(executor.getStatus(), is(AnalysisStatus.RUNNING));

    await().until(moduleCalled(dependencyModuleInstance));

    assertThat(dependencyModuleState.getThread(), is(not(nullValue())));
    assertThat(dependencyModuleState.getThread().isAlive(), is(true));
    
    await().until(moduleExecuted(dependencyModuleInstance));

    assertThat(dependencyModuleState.getThread().isAlive(), is(false));
    assertThat(targetModuleState.getResultProvider().getResultFor(Integer.class),
        is(IntProvidingModule.RESULT));
    assertThat(targetModuleState.isExecutable(), is(true));

    await().until(moduleCalled(targetModuleInstance));

    assertThat(targetModuleState.getThread(), is(not(nullValue())));
    assertThat(targetModuleState.getThread().isAlive(), is(true));

    await().until(statusIs(AnalysisStatus.FINISHED));

    assertThat(targetModuleState.getThread().isAlive(), is(false));
    assertThat(targetModuleInstance.hasBeenExecuted(), is(true));
  }

  private Callable<Boolean> moduleExecuted(final DebugBaseModule<?> instance) {
    return new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        return instance.hasBeenExecuted();
      }
    };
  }

  private Callable<Boolean> moduleCalled(final DebugBaseModule<?> instance) {
    return new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        return instance.hasBeenCalled();
      }
    };
  }

  private Callable<Boolean> statusIs(final AnalysisStatus status) {
    return new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        return executor.getStatus() == status;
      }
    };
  }
}
