package de.unistuttgart.vis.vita.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * The AnalysisController resolves the dependencies of every module. It also provides a optimized
 * order corresponding to the computers cores.
 *
 * The controller starts and cancels the analysis.
 */
public class AnalysisController {

  private Model model;
  private ModuleRegistry moduleRegistry;
  private ModuleResultProvider moduleResultProvider;
  private boolean analyseRunning;



  /**
   * New instance of the controller with given model. It will be created a new empty module
   * registry.
   * 
   * @param model The model with data.
   */
  public AnalysisController(Model model) {
    this.model = model;
    moduleRegistry = ModuleRegistry.getDefaultRegistry();
    moduleResultProvider = new ModuleResultProvider();
  }

  /**
   * New instance of the controller with given model and registry.
   * 
   * @param model The model to use.
   * @param moduleRegistry The registry to use.
   */
  public AnalysisController(Model model, ModuleRegistry moduleRegistry) {
    this.model = model;
    this.moduleRegistry = moduleRegistry;
    moduleResultProvider = new ModuleResultProvider();
  }

  /**
   * Starts the schedule of all modules registered in the registry. It calculates which modules can
   * be started first and which have to wait for other modules. This algorithm also checks how many
   * cores the CPU has and optimize it for multi-threading.
   * 
   * @param filepath The path to the document.
   * @return The document id.
   */
  public String scheduleDocumentAnalysis(Path filepath) {
    return null;
  }

  /**
   * Generates a list with module objects.
   * 
   * @return List with modules.
   * @throws IllegalStateException If any object creation fails.
   */
  private List<Module<?>> getModuleInstances() throws IllegalStateException {
    List<Module<?>> moduleList = new ArrayList<>();
    List<Class<? extends Module<?>>> moduleClassList = moduleRegistry.getModules();

    for (Class<?> currentClass : moduleClassList) {
      Constructor<?> moduleConstructor = null;
      Object theModule = null;

      try {
        moduleConstructor = currentClass.getConstructor();
        theModule = moduleConstructor.newInstance();
      } catch (NoSuchMethodException | SecurityException | InstantiationException
          | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        throw new IllegalStateException("Not successful creation.");
      }

      moduleList.add((Module<?>) theModule);
    }

    return moduleList;
  }

  /**
   * Starts the analyze for the document.
   * 
   */
  public void startAnalysis() {
    if (analyseRunning) {
      return;
    }

    analyseRunning = true;
    int processors = Runtime.getRuntime().availableProcessors();

    for (int i = 0; i < processors; i++) {
      // TODO each thread takes out a module and executes it
      Thread theThread = new ModuleExecutionThread();
      theThread.start();
    }
  }

  /**
   * Cancels the analysis if necessary. The process will be canceled as soon as possible.
   * 
   * @param documentID The document which is analyzed.
   */
  public void cancelAnalysis(String documentID) {

  }

  /**
   * 
   * @param documentId
   */
  public void restartAnalysis(String documentId) {

  }
}
