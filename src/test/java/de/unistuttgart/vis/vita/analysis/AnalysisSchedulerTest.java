package de.unistuttgart.vis.vita.analysis;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.analysis.mockmodules.DualDependentModule;
import de.unistuttgart.vis.vita.analysis.mockmodules.IntProvidingModule;
import de.unistuttgart.vis.vita.analysis.mockmodules.ManualModule;
import de.unistuttgart.vis.vita.analysis.mockmodules.MockModule;

public class AnalysisSchedulerTest {
  private AnalysisScheduler scheduler;
  private ModuleRegistry registry;

  @Before
  public void setUp() {
    registry = mock(ModuleRegistry.class);
  }

  @Test
  public void testSimple() {
    ModuleClass moduleClass = ModuleClass.get(IntProvidingModule.class);
    scheduler = new AnalysisScheduler(registry, moduleClass);

    Collection<ModuleExecutionState> scheduled = scheduler.getScheduledModules();

    assertThat(scheduled, hasSize(1));
    ModuleExecutionState singleState = scheduled.iterator().next();
    assertThat(singleState.getModuleClass(), is(moduleClass));
    assertThat(singleState.getRemainingDependencies(), is(empty()));
    assertThat(singleState.getDirectAndIndirectDependencies(), is(empty()));
    assertThat(singleState.isExecutable(), is(true));
  }

  @Test
  public void testSingleDependency() {
    ModuleClass targetModule = ModuleClass.get(MockModule.class);
    ModuleClass dependencyModule = ModuleClass.get(IntProvidingModule.class);
    when(registry.getModuleClassFor(Integer.class)).thenReturn(dependencyModule);
    scheduler = new AnalysisScheduler(registry, targetModule);

    Collection<ModuleExecutionState> scheduled = scheduler.getScheduledModules();

    assertThat(scheduled, hasSize(2));
    ModuleExecutionState targetState = findModuleExecutionState(scheduled, targetModule);
    assertThat(targetState.getRemainingDependencies(), containsInAnyOrder(dependencyModule));
    assertThat(targetState.getDirectAndIndirectDependencies(), containsInAnyOrder(dependencyModule));
    assertThat(targetState.isExecutable(), is(false));

    ModuleExecutionState dependencyState = findModuleExecutionState(scheduled, dependencyModule);
    assertThat(dependencyState.getRemainingDependencies(), is(empty()));
    assertThat(dependencyState.getDirectAndIndirectDependencies(), is(empty()));
    assertThat(dependencyState.isExecutable(), is(true));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testPassingModuleInstances() {
    ModuleClass targetModule = ModuleClass.get(MockModule.class);
    ModuleClass dependencyModule = ModuleClass.get(IntProvidingModule.class);
    Module<Integer> dependencyModuleInstance = (Module<Integer>) dependencyModule.newInstance();
    when(registry.getModuleClassFor(Integer.class)).thenReturn(dependencyModule);

    scheduler = new AnalysisScheduler(registry, targetModule, dependencyModuleInstance);
    Collection<ModuleExecutionState> scheduled = scheduler.getScheduledModules();

    assertThat(scheduled, hasSize(2));
    ModuleExecutionState dependencyState = findModuleExecutionState(scheduled, dependencyModule);
    assertThat((Module<Integer>) dependencyState.getInstance(),
        is(sameInstance(dependencyModuleInstance)));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testPassingInstanceOfTargetModule() {
    ModuleClass targetModule = ModuleClass.get(MockModule.class);
    ModuleClass dependencyModule = ModuleClass.get(IntProvidingModule.class);
    Module<String> targetModuleInstance = (Module<String>) targetModule.newInstance();
    when(registry.getModuleClassFor(Integer.class)).thenReturn(dependencyModule);

    scheduler = new AnalysisScheduler(registry, targetModule, targetModuleInstance);
    Collection<ModuleExecutionState> scheduled = scheduler.getScheduledModules();

    assertThat(scheduled, hasSize(2));
    ModuleExecutionState targetState = findModuleExecutionState(scheduled, targetModule);
    assertThat((Module<String>) targetState.getInstance(), is(sameInstance(targetModuleInstance)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPassingTwoInstanceOfSameModuleThrows() {
    ModuleClass targetModule = ModuleClass.get(IntProvidingModule.class);
    Module<?> moduleInstance1 = targetModule.newInstance();
    Module<?> moduleInstance2 = targetModule.newInstance();

    scheduler = new AnalysisScheduler(registry, targetModule, moduleInstance1, moduleInstance2);
  }

  @Test(expected = UnresolvedModuleDependencyException.class)
  public void testMissingInstanceThrows() {
    ModuleClass targetModule = ModuleClass.get(ManualModule.class);
    ModuleClass dependencyModule = ModuleClass.get(IntProvidingModule.class);
    when(registry.getModuleClassFor(Integer.class)).thenReturn(dependencyModule);

    // should complain that the instance of ManualMode is missing because there is no parameterless
    // constructor for that module.
    scheduler = new AnalysisScheduler(registry, targetModule);
  }

  @Test(expected = UnresolvedModuleDependencyException.class)
  public void testMissingInstanceThrows2() {
    ModuleClass targetModule = ModuleClass.get(MockModule.class);
    ModuleClass dependencyModule = ModuleClass.get(ManualModule.class);
    when(registry.getModuleClassFor(Integer.class)).thenReturn(dependencyModule);

    // should complain that the instance of ManualMode is missing because there is no parameterless
    // constructor for that module.
    scheduler = new AnalysisScheduler(registry, targetModule);
  }

  @Test(expected = UnresolvedModuleDependencyException.class)
  public void testMissingDependenyThrows() {
    ModuleClass targetModule = ModuleClass.get(MockModule.class);
    
    scheduler = new AnalysisScheduler(registry, targetModule);
  }

  @Test
  public void testFourModules() {
    ModuleClass targetModule = ModuleClass.get(DualDependentModule.class);
    ModuleClass intModule = ModuleClass.get(IntProvidingModule.class);
    ModuleClass stringModule = ModuleClass.get(MockModule.class);
    ModuleClass boolModule = ModuleClass.get(ManualModule.class);
    when(registry.getModuleClassFor(Integer.class)).thenReturn(intModule);
    when(registry.getModuleClassFor(String.class)).thenReturn(stringModule);
    when(registry.getModuleClassFor(Boolean.class)).thenReturn(boolModule);

    scheduler = new AnalysisScheduler(registry, targetModule, new ManualModule(123));
    Collection<ModuleExecutionState> scheduled = scheduler.getScheduledModules();

    ModuleExecutionState targetState = findModuleExecutionState(scheduled, targetModule);
    assertThat(targetState.isExecutable(), is(false));
    assertThat(targetState.getRemainingDependencies(), containsInAnyOrder(stringModule, boolModule));
    assertThat(targetState.getDirectAndIndirectDependencies(),
        containsInAnyOrder(stringModule, boolModule, intModule));
    
    ModuleExecutionState intState = findModuleExecutionState(scheduled, intModule);
    assertThat(intState.isExecutable(), is(true));
    assertThat(intState.getRemainingDependencies(), is(empty()));
    assertThat(intState.getDirectAndIndirectDependencies(), is(empty()));
    
    ModuleExecutionState stringState = findModuleExecutionState(scheduled, stringModule);
    assertThat(stringState.isExecutable(), is(false));
    assertThat(stringState.getRemainingDependencies(), containsInAnyOrder(intModule));
    assertThat(stringState.getDirectAndIndirectDependencies(), containsInAnyOrder(intModule));

    ModuleExecutionState boolState = findModuleExecutionState(scheduled, boolModule);
    assertThat(boolState.isExecutable(), is(false));
    assertThat(boolState.getRemainingDependencies(), containsInAnyOrder(intModule));
    assertThat(boolState.getDirectAndIndirectDependencies(), containsInAnyOrder(intModule));
  }

  private ModuleExecutionState findModuleExecutionState(Iterable<ModuleExecutionState> list,
      ModuleClass needle) {
    for (ModuleExecutionState item : list) {
      if (item.getModuleClass().equals(needle))
        return item;
    }
    fail("ModuleExecutionState for module " + needle + " missing");
    return null; // will never happen
  }
}
