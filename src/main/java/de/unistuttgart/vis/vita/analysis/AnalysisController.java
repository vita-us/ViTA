package de.unistuttgart.vis.vita.analysis;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import de.unistuttgart.vis.vita.analysis.modules.ImportModule;
import de.unistuttgart.vis.vita.analysis.modules.MainAnalysisModule;
import de.unistuttgart.vis.vita.analysis.modules.ModelProviderModule;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;

/**
 * The AnalysisController resolves the dependencies of every module. It also provides a optimized
 * order corresponding to the computers cores.
 *
 * The controller starts and cancels the analysis.
 */
public class AnalysisController {
  private static final Class<?> TARGET_MODULE = MainAnalysisModule.class;
  
  private Model model;
  private ModuleRegistry moduleRegistry;
  
  private Queue<Document> analysisQueue = new PriorityQueue<>();
  private boolean isAnalysisRunning;
  private AnalysisExecutor currentExecuter;
  private Document currentDocument;
  
  /**
   * Stores the document file locations for each document id
   */
  private Map<String, Path> documentPaths = new HashMap<>();
  
  /**
   * New instance of the controller with given model. It will be created a new empty module
   * registry.
   * 
   * @param model The model with data.
   */
  public AnalysisController(Model model) {
    this(model, ModuleRegistry.getDefaultRegistry());
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
  }

  /**
   * Starts the schedule of all modules registered in the registry. It calculates which modules can
   * be started first and which have to wait for other modules. This algorithm also checks how many
   * cores the CPU has and optimize it for multi-threading.
   *
   * @param filePath The path to the document.
   * @return The document id.
   */
  public synchronized String scheduleDocumentAnalysis(Path filePath) {
    Document document = createDocument(filePath);
    documentPaths.put(document.getId(), filePath);
    scheduleDocumentAnalyisis(document);
    return document.getId();
  }
  
  private synchronized void scheduleDocumentAnalyisis(Document document) {
    if (isAnalysisRunning) {
      analysisQueue.add(document);
    } else {
      startAnalysis(document);
    }
  }
  
  private synchronized void startAnalysis(Document document) {
    Path path = documentPaths.get(document.getId());
    AnalysisScheduler scheduler = new AnalysisScheduler(moduleRegistry, ModuleClass.get(TARGET_MODULE),
        new ImportModule(path), new ModelProviderModule(model));
    AnalysisExecutor executor = new AnalysisExecutor(scheduler.getScheduledModules());
    executor.start();
    currentDocument = document;
    isAnalysisRunning = true;
    
    // TODO get notified when finished
  }
  
  private Document createDocument(Path filePath) {
    // TODO
    return null;
  }

  /**
   * Cancels the analysis if necessary. The process will be canceled as soon as possible.
   * 
   * @param documentID The document which is analyzed.
   */
  public synchronized void cancelAnalysis(String documentID) {
    if (currentDocument != null && currentDocument.getId().equals(documentID)) {
      currentExecuter.cancel();
    } else {
      Iterator<Document> it = analysisQueue.iterator();
      while (it.hasNext()) {
        if (it.next().getId().equals(documentID)) {
          it.remove();
          return;
        }
      }
    }
  }

  /**
   * 
   * @param documentId
   */
  public void restartAnalysis(String documentId) {
    Document document = null; // TODO find in database
    scheduleDocumentAnalyisis(document);
  }
  
  public synchronized int documentsInQueue() {
    return analysisQueue.size();
  }

  public synchronized boolean isWorking() {
    return currentExecuter != null;
  }
}
