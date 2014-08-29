package de.unistuttgart.vis.vita.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

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
  
  private AnalysisStatus status = AnalysisStatus.NOT_STARTED;
  /**
   * Creates an executor for the scheduled modules
   * @param scheduledModules the modules to execute
   */
  AnalysisExecutor(Iterable<ModuleExecutionState> scheduledModules) {
    this.scheduledModules = Lists.newArrayList(scheduledModules);
    runningModules = new ArrayList<>();
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
      case CANCELLED:
        throw new IllegalStateException("Cannot restart a cancelled executer");
      case FAILED:
        throw new IllegalStateException("Cannot restart a failed executer");
      case RUNNING:
        return;
      case NOT_STARTED:
        status = AnalysisStatus.RUNNING;
        startExecutableModules();
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
    
    // Check if there is a dependency deadlock, i.e. there are remaining modules, but none could execute
    if (scheduledModules.size() > 0 && runningModules.size() == 0) {
      status = AnalysisStatus.FAILED;
      // TODO
    }
  }
  
  /**
   * Starts a thread executing the module
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
          result = instance.execute(resultProvider, null /* TODO  */);
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
    runningModules.remove(moduleState);
    for (ModuleExecutionState module : scheduledModules) {
      module.notifyDependencyFinished(moduleState.getModuleClass(), result);
    }
    startExecutableModules();

    if (scheduledModules.size() == 0 && runningModules.size() == 0) {
      status = AnalysisStatus.FINISHED;
    }
  }

  private synchronized void onModuleFailed(ModuleExecutionState moduleState, Exception e) {
    // TODO
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
}
