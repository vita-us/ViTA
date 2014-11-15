package de.unistuttgart.vis.vita.analysis;

import static com.jayway.awaitility.Awaitility.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import de.unistuttgart.vis.vita.analysis.mockmodules.DebugBaseModule;
import de.unistuttgart.vis.vita.analysis.mockmodules.IntProvidingModule;
import de.unistuttgart.vis.vita.analysis.mockmodules.MockModule;

public class AnalysisExecutorTest {
  private ModuleClass targetModule;
  private ModuleClass dependencyModule;
  private ModuleExecutionState targetModuleState;
  private MockModule targetModuleInstance;
  private IntProvidingModule dependencyModuleInstance;
  private ModuleExecutionState dependencyModuleState;
  private AnalysisExecutor executor;
  private AnalysisObserver observer;
  
  @Before
  public void setUp() {
    targetModule = ModuleClass.get(MockModule.class);
    targetModuleInstance = spy(new MockModule());
    dependencyModule = ModuleClass.get(IntProvidingModule.class);
    dependencyModuleInstance = new IntProvidingModule();
    targetModuleState =
        new ModuleExecutionState(targetModule, targetModuleInstance,
            ImmutableSet.of(dependencyModule), ImmutableSet.of(dependencyModule));
    Set<ModuleClass> empty = Collections.emptySet();
    dependencyModuleState =
        new ModuleExecutionState(dependencyModule, dependencyModuleInstance, empty, empty);
    executor = new AnalysisExecutor(Arrays.asList(targetModuleState, dependencyModuleState));
    observer = mock(AnalysisObserver.class);
    executor.addObserver(observer);
  }

  @Test
  public void testRunning() throws InterruptedException {
    assertThat(executor.getStatus(), is(AnalysisStatus.READY));
    assertThat(targetModuleState.getProgress(), is(0.0));
    
    executor.start();
    assertThat(executor.getStatus(), is(AnalysisStatus.RUNNING));

    await().until(moduleCalled(dependencyModuleInstance));

    assertThat(dependencyModuleState.getThread(), is(not(nullValue())));
    assertThat(dependencyModuleState.getThread().isAlive(), is(true));
    assertThat(targetModuleInstance.getCurrentProgress(), is(0.0));
    
    await().until(moduleProgressIs(targetModuleInstance, 0.25));
    await().until(moduleExecuted(dependencyModuleInstance));

    verify(targetModuleInstance).dependencyFinished(Integer.class, IntProvidingModule.RESULT);
    assertThat(targetModuleState.getResultProvider().getResultFor(Integer.class),
        is(IntProvidingModule.RESULT));
    assertThat(targetModuleState.isExecutable(), is(true));
    assertThat(targetModuleInstance.getCurrentProgress(), is(0.5));
    assertThat(targetModuleState.getProgress(), is(0.5));

    await().until(moduleCalled(targetModuleInstance));

    assertThat(targetModuleState.getThread(), is(not(nullValue())));
    assertThat(targetModuleState.getThread().isAlive(), is(true));

    await().until(statusIs(AnalysisStatus.FINISHED));
    Thread.sleep(100); // avoid race condition
    verify(observer).onFinish(executor);
    verifyNoMoreInteractions(observer);

    assertThat(targetModuleInstance.hasBeenExecuted(), is(true));
    assertThat(executor.getFailedModules().values(), is(empty()));
    assertThat(targetModuleInstance.getCurrentProgress(), is(1.0));
    assertThat(targetModuleState.getProgress(), is(1.0));
  }

  @Test
  public void testCancel() throws InterruptedException {
    executor.start();

    await().until(moduleCalled(dependencyModuleInstance));

    executor.cancel();

    assertThat(executor.getStatus(), is(AnalysisStatus.CANCELLED));

    await().until(moduleInterrupted(dependencyModuleInstance));

    // Make sure this does not get called
    Thread.sleep(DebugBaseModule.DEFAULT_SLEEP_MS * 3);
    assertThat(targetModuleInstance.hasBeenCalled(), is(false));
    assertThat(targetModuleState.isExecutable(), is(false));
  }

  @Test
  public void testCancelStaysCanelledEvenWhenModulesFinish() throws InterruptedException {
    executor.start();

    await().until(moduleCalled(targetModuleInstance));

    executor.cancel();

    assertThat(executor.getStatus(), is(AnalysisStatus.CANCELLED));
    await().until(moduleInterrupted(targetModuleInstance));
    assertThat(executor.getStatus(), is(AnalysisStatus.CANCELLED));
    await().until(moduleExecuted(targetModuleInstance));
    assertThat(executor.getStatus(), is(AnalysisStatus.CANCELLED));
    await().until(moduleInterrupted(targetModuleInstance));
    assertThat(executor.getStatus(), is(AnalysisStatus.CANCELLED));
  }

  @Test
  public void testFailingModule() throws InterruptedException {
    targetModuleInstance.makeFail();
    executor.start();
    await().until(moduleCalled(targetModuleInstance));

    await().until(statusIs(AnalysisStatus.FAILED));
    assertThat(executor.getFailedModules(), hasKey(targetModule));
    assertEquals(DebugBaseModule.FAIL_EXCEPTION, executor.getFailedModules().get(targetModule)
        .getClass());
    Thread.sleep(100); // avoid race condition
    verify(observer).onFail(executor);
    verifyNoMoreInteractions(observer);
  }

  @Test
  public void testFailingDependency() throws InterruptedException {
    dependencyModuleInstance.makeFail();
    executor.start();
    await().until(moduleCalled(dependencyModuleInstance));

    await().until(statusIs(AnalysisStatus.FAILED));
    assertThat(executor.getFailedModules(), hasKey(dependencyModule));
    assertEquals(DebugBaseModule.FAIL_EXCEPTION, executor.getFailedModules().get(dependencyModule)
        .getClass());
    Thread.sleep(100); // avoid race condition
    verify(observer).onFail(executor);
    verifyNoMoreInteractions(observer);
    verify(targetModuleInstance).dependencyFailed(Integer.class);

    // Make sure that the dependent module has not been called (the sleep above is important)
    assertThat(targetModuleInstance.hasBeenCalled(), is(false));
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

  private Callable<Boolean> moduleInterrupted(final DebugBaseModule<?> instance) {
    return new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        return instance.hasBeenInterrupted();
      }
    };
  }

  private Callable<Boolean> moduleProgressIs(final DebugBaseModule<?> instance, final double progress) {
    return new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        return Math.abs(instance.getCurrentProgress() - progress) < 0.001;
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
