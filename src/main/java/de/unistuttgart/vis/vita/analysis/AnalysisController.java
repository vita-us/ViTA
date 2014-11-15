package de.unistuttgart.vis.vita.analysis;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;

/**
 * Maintains a document queue and controls start and stop of their analysis
 */
@ApplicationScoped
public class AnalysisController {
  @Inject
  private Model model;
  
  private AnalysisExecutorFactory executorFactory;
  
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
  @Inject
  public AnalysisController(Model model) {
    this(model, ModuleRegistry.getDefaultRegistry());
  }
  
  /**
   * This constructor should not be used manually, only by the CDI framework
   */
  public AnalysisController() {
    // Model will be set by CDI framework
    this(null);
  }

  /**
   * New instance of the controller with given model and registry.
   * 
   * @param model The model to use.
   * @param moduleRegistry The registry to use.
   */
  public AnalysisController(Model model, ModuleRegistry moduleRegistry) {
    this(model, new DefaultAnalysisExecutorFactory(model, moduleRegistry));
  }

  public AnalysisController(Model model, AnalysisExecutorFactory executorFactory) {
    this.model = model;
    this.executorFactory = executorFactory;
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
    currentExecuter = executorFactory.createExecutor(document.getId(), path);
    currentExecuter.start();
    currentDocument = document;
    isAnalysisRunning = true;
    currentExecuter.addObserver(new AnalysisObserver() {
      @Override
      public void onFinish(AnalysisExecutor executor) {
        startNextAnalysis();
      }

      @Override
      public void onFail(AnalysisExecutor executor) {
        startNextAnalysis();
      }
    });
  }
  
  private Document createDocument(Path filePath) {
    Document document = new Document();
    document.getMetadata().setTitle(filePath.getFileName().toString());
    EntityManager em = model.getEntityManager();
    em.getTransaction().begin();
    em.persist(document);
    em.getTransaction().commit();
    em.close();
    return document;
  }

  /**
   * Cancels the analysis if necessary. The process will be canceled as soon as possible.
   * 
   * @param documentID The document which is analyzed.
   */
  public synchronized void cancelAnalysis(String documentID) {
    if (currentDocument != null && currentDocument.getId().equals(documentID)) {
      currentExecuter.cancel();
      currentExecuter = null;
      startNextAnalysis();
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
   * Continues processing the queue after an analysis has finished or failed
   */
  private synchronized void startNextAnalysis() {
    if (analysisQueue.isEmpty()) {
      isAnalysisRunning = false;
      return;
    }

    startAnalysis(analysisQueue.remove());
  }

  /**
   * Restarts a previously cancelled or failed document analysis
   * 
   * @param documentId
   */
  public void restartAnalysis(String documentId) {
    EntityManager em = model.getEntityManager();
    TypedQuery<Document> query = em.createNamedQuery("Document.findDocumentById", Document.class);
    query.setParameter("documentId", documentId);
    List<Document> documents = query.getResultList();
    if (documents.isEmpty()) {
      throw new IllegalArgumentException("No such document found");
    }
    Document document = documents.get(0);
    em.close();

    scheduleDocumentAnalyisis(document);
  }
  
  /**
   * Gets the number of documents that are currently waiting for being analyzed. The document
   * currently being analyzed does not count to this value.
   * 
   * @return the number of documents in queue
   */
  public synchronized int documentsInQueue() {
    return analysisQueue.size();
  }

  /**
   * Indicates whether a document is being analyzed at the moment
   * 
   * @return true, if an analysis is in process, false otherwise
   */
  public synchronized boolean isWorking() {
    return isAnalysisRunning;
  }
}
