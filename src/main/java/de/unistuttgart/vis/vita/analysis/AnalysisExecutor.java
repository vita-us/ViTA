package de.unistuttgart.vis.vita.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;

/**
 * Controls the execution of the analysis of a single document
 */
public class AnalysisExecutor {
  /**
   * Stores the modules that have not yet been started, with the modules they are waiting for
   */
  private List<ModuleExecutionState> scheduledModules;
  
  /**
   * Stores the modules that are currently executing
   */
  private List<ModuleExecutionState> runningModules;
  
  /**
   * Stores the modules that have encountered an exception and their exception
   */
  private Map<ModuleClass, Exception> failedModules = new HashMap<>();

  private AnalysisStatus status = AnalysisStatus.READY;

  private List<AnalysisObserver> observers = new ArrayList<>();

  /**
   * Creates an executor for the scheduled modules
   * @param scheduledModules the modules to execute
   */
  AnalysisExecutor(Iterable<ModuleExecutionState> scheduledModules) {
    this.scheduledModules = Lists.newArrayList(scheduledModules);
    runningModules = new ArrayList<>();
  }
  
  /**
   * Adds an observer that will be notified when the analysis fails or succeeds
   * 
   * @param observer
   */
  public void addObserver(AnalysisObserver observer) {
    observers.add(observer);
  }

  /**
   * Gets the status of this executor
   * 
   * @return the status
   */
  public AnalysisStatus getStatus() {
    return status;
  }

  public synchronized void start() {
    switch (status) {
      case FINISHED:
      case CANCELLED:
      case FAILED:
        throw new IllegalStateException("Cannot restart an executer");
      case READY:
        status = AnalysisStatus.RUNNING;
        startExecutableModules();
        break;
      case RUNNING:
        // do nothing
        break;
    }
  }
  
  /**
   * Starts all modules whose dependencies are finished
   */
  private synchronized void startExecutableModules() {
    Iterator<ModuleExecutionState> it = scheduledModules.iterator();
    while (it.hasNext()) {
      ModuleExecutionState moduleState = it.next();
      if (moduleState.isExecutable()) {
        startModuleExecution(moduleState);
        it.remove();
      }
    }
    
    // Check if there is a dependency deadlock (there are remaining modules, but none could execute)
    if (scheduledModules.size() > 0 && runningModules.size() == 0) {
      Exception ex =
          new UnresolvedModuleDependencyException(
              "The module could not be executed because of a deadlock.");
      for (ModuleExecutionState module : scheduledModules) {
        failedModules.put(module.getModuleClass(), ex);
      }
      setStatus(AnalysisStatus.FAILED);
    }
  }
  
  /**
   * Sets the status and notifies the observers in certain statuses
   * 
   * @param status the new status
   */
  private void setStatus(AnalysisStatus status) {
    if (status != this.status) {
      this.status = status;
      switch (status) {
        case FAILED:
          for (AnalysisObserver observer : observers) {
            observer.onFail(this);
          }
          break;
        case FINISHED:
          for (AnalysisObserver observer : observers) {
            observer.onFinish(this);
          }
          break;
        default:
          // do nothing
      }
    }
  }

  /**
   * Starts a thread executing the module
   * 
   * @param moduleState the module to execute
   */
  private synchronized void startModuleExecution(final ModuleExecutionState moduleState) {
    final ModuleResultProvider resultProvider = moduleState.getResultProvider();
    final Module<?> instance = moduleState.getInstance();

    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        Object result;
        try {
          result = instance.execute(resultProvider, new ProgressListener() {
            @Override
            public void observeProgress(double progress) {
              onModuleProgress(moduleState, progress);
            }
          });
        } catch(Exception e) {
          onModuleFailed(moduleState, e);
          return;
        }
        onModuleFinished(moduleState, result);
      }
    });
    thread.start();
    moduleState.setThread(thread);
    runningModules.add(moduleState);
  }
  
  private synchronized void onModuleFinished(ModuleExecutionState moduleState, Object result) {
    // Ignore results produced after failure / cancel
    if (status != AnalysisStatus.RUNNING)
      return;

    for (ModuleExecutionState module : getActiveModules()) {
      module.notifyModuleFinished(moduleState.getModuleClass(), result);
    }
    runningModules.remove(moduleState);
    startExecutableModules();
    checkFinished();
  }
  
  private synchronized void onModuleProgress(ModuleExecutionState moduleState, double progress) {
    // Ignore calls after failure / cancel
    if (status != AnalysisStatus.RUNNING)
      return;

    for (ModuleExecutionState module : getActiveModules()) {
      module.notifyModuleProgress(moduleState.getModuleClass(), progress);
    }
  }

  private synchronized void onModuleFailed(ModuleExecutionState moduleState, Exception e) {
    // Ignore results produced after failure / cancel
    if (status != AnalysisStatus.RUNNING)
      return;

    runningModules.remove(moduleState);
    failedModules.put(moduleState.getModuleClass(), e);
    removeModuleAndDependencies(moduleState);
    checkFinished();
  }
  
  /**
   * Gets the scheduled and running modules
   * @return an iterable
   */
  private synchronized Iterable<ModuleExecutionState> getActiveModules() {
    return Iterables.concat(scheduledModules, runningModules);
  }

  private void checkFinished() {
    if (scheduledModules.size() == 0 && runningModules.size() == 0) {
      if (failedModules.isEmpty()) {
        setStatus(AnalysisStatus.FINISHED);
      } else {
        setStatus(AnalysisStatus.FAILED);
      }
    }
  }

  /**
   * Recursively removes the module and all modules that depend on it
   * 
   * @param moduleToRemove the module to remove
   */
  private void removeModuleAndDependencies(ModuleExecutionState moduleToRemove) {
    scheduledModules.remove(moduleToRemove);
    List<ModuleExecutionState> scheduledModulesCopy = ImmutableList.copyOf(scheduledModules);
    for (ModuleExecutionState module : scheduledModulesCopy) {
      if (module.getRemainingDependencies().contains(moduleToRemove.getModuleClass())) {
        removeModuleAndDependencies(module);
      }
    }
  }
  
  /**
   * Stops the execution, interrupting all running modules and preventing scheduled modules from
   * starting
   */
  public void cancel() {
    if (status != AnalysisStatus.FAILED) {
      status = AnalysisStatus.CANCELLED;
    }

    for (ModuleExecutionState state : runningModules) {
      state.getThread().interrupt();
    }
    scheduledModules.clear();
  }

  /**
   * Gets a map of objects that have failed and the exceptions they have thrown
   * 
   * @return the failed exceptions; is empty if successful
   */
  public Map<ModuleClass, Exception> getFailedModules() {
    return Collections.unmodifiableMap(failedModules);
  }
}
