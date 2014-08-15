package de.unistuttgart.vis.vita.analysis;

import de.unistuttgart.vis.vita.analysis.modules.ImportModule;
import de.unistuttgart.vis.vita.analysis.modules.MainAnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

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
  private Queue<Document> analysisQueue = new PriorityQueue<>();
  private Map<Class<?>, Class<? extends Module>> resultToClassMapping;
  private Map<Class<? extends Module>, Collection<Class<? extends Module>>> dependencyMapping;

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
   * @param filePath The path to the document.
   * @return The document id.
   */
  public String scheduleDocumentAnalysis(Path filePath) {
    // TODO register document in the queue

    resultToClassMapping = new HashMap<>();
    dependencyMapping = new HashMap<>();

    resultToClassMapping.put(ImportResult.class, ImportModule.class);
    List<Class<? extends Module>> emptyList = new ArrayList<>();
    dependencyMapping.put(ImportModule.class, emptyList);

    // Loads all module recursivly.
    addModule(new MainAnalysisModule());

    /* Execute all possible modules. */
    for (Map.Entry<Class<? extends Module>, Collection<Class<? extends Module>>> currentClass : dependencyMapping
        .entrySet()) {
      if (currentClass.getValue().isEmpty()) {
        dependencyMapping.remove(currentClass.getKey());
        Class<? extends Module> executableModule = currentClass.getKey();

        new ModuleExecutionThread(this, executableModule).start();
      }
    }

    return null;
  }

  public synchronized void continueAnalysis(Class<? extends Class> finishedClass) {
    /* Remove the executed class from the dependencies. */
    for (Map.Entry<Class<? extends Module>, Collection<Class<? extends Module>>> currentClass : dependencyMapping
        .entrySet()) {
      Collection<?> bla = currentClass.getValue();
      bla.remove(finishedClass);
    }

    /* Execute all possible modules. */
    for (Map.Entry<Class<? extends Module>, Collection<Class<? extends Module>>> currentClass : dependencyMapping
        .entrySet()) {
      if (currentClass.getValue().isEmpty()) {
        dependencyMapping.remove(currentClass.getKey());
        Class<? extends Module> executableModule = currentClass.getKey();

        new ModuleExecutionThread(this, executableModule).start();
      }
    }

    if(dependencyMapping.isEmpty()) {
      // TODO analyse is finished
    }
  }

  private void addModule(Module mainAnalysisModule) {
    resultToClassMapping.put(moduleResultProvider.getResultClassFor(mainAnalysisModule),
                             mainAnalysisModule.getClass());
    Collection<Class<? extends Module>> dependencies = mainAnalysisModule.getDependencies();

    for (Class<?> currentClass : dependencies) {
      Class<?> resultClass = moduleResultProvider.getResultClassFor(mainAnalysisModule);

      if (!resultToClassMapping.containsKey(resultClass)) {
        try {
          Module newModule = (Module) currentClass.getConstructor().newInstance();
          addModule(newModule);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
          e.printStackTrace();
        }
      }

    }

    dependencyMapping.put(mainAnalysisModule.getClass(), dependencies);
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
      Thread theThread = new Thread();
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
  
  public int documentsInQueue() {
    return analysisQueue.size();
  }

  public Boolean isWorking() {
    return analyseRunning;
  }

  public ModuleResultProvider getResultProvider() {
    return moduleResultProvider;
  }
}
