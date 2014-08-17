package de.unistuttgart.vis.vita.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
  
  private boolean isStarted = false;
  
  /**
   * Creates an executor for the scheduled modules
   * @param scheduledModules the modules to execute
   */
  AnalysisExecutor(Iterable<ModuleExecutionState> scheduledModules) {
    this.scheduledModules = new ArrayList<>(this.scheduledModules);
  }
  
  public synchronized void start() {
    if (!isStarted) {
      isStarted = true;
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
    runningModules.add(moduleState);
  }
  
  private synchronized void onModuleFinished(ModuleExecutionState moduleState, Object result) {
    for (ModuleExecutionState module : scheduledModules) {
      module.notifyDependencyFinished(moduleState.getModuleClass(), result);
    }
    startExecutableModules();
  }

  private synchronized void onModuleFailed(ModuleExecutionState moduleState, Exception e) {
    // TODO
  }
  
  public void cancel() {
    // TODO
  }
}
