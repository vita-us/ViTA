package de.unistuttgart.vis.vita.analysis;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;
import de.unistuttgart.vis.vita.model.document.Document;

import java.nio.file.Path;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

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
   * @param fileName The original document name.
   * @return The document id.
   */
  public String scheduleDocumentAnalysis(Path filePath, String fileName) {
    return scheduleDocumentAnalysis(filePath, fileName, new AnalysisParameters());
  }

  /**
   * Starts the schedule of all modules registered in the registry. It calculates which modules can
   * be started first and which have to wait for other modules. This algorithm also checks how many
   * cores the CPU has and optimize it for multi-threading.
   *
   * @param filePath The path to the document.
   * @param fileName The original document name.
   * @param parameters parametrization of the analysis
   * @return The document id.
   */
  public synchronized String scheduleDocumentAnalysis(Path filePath, String fileName,
      AnalysisParameters parameters) {
    Document document = createDocument(filePath, fileName);
    document.setParameters(parameters);
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

  private synchronized void startAnalysis(final Document document) {
    setStatus(document.getId(),  AnalysisStatus.RUNNING);
    Path path = document.getFilePath();
    if (path == null)
      throw new UnsupportedOperationException("There is no file associated with the document");
    currentExecuter = executorFactory.createExecutor(document.getId(), path,
        document.getParameters());
    currentExecuter.start();
    currentDocument = document;
    isAnalysisRunning = true;
    currentExecuter.addObserver(new AnalysisObserver() {
      @Override
      public void onFinish(AnalysisExecutor executor) {
        setStatus(document.getId(),  AnalysisStatus.FINISHED);
        startNextAnalysis();
      }

      @Override
      public void onFail(AnalysisExecutor executor) {
        setStatus(document.getId(),  AnalysisStatus.FAILED);
        startNextAnalysis();
      }
    });
  }

  private Document createDocument(Path filePath, String fileName) {
    Document document = new Document();
    document.getMetadata().setTitle(fileName);
    document.setFileName(fileName);
    document.getProgress().setStatus(AnalysisStatus.READY);
    document.setFilePath(filePath);
    document.setUploadDate(new Date());

    EntityManager em = null;
    try {
      em = model.getEntityManager();
      em.getTransaction().begin();
      em.persist(document);
      em.getTransaction().commit();
    } finally {
      if (em != null) {
        em.close();
      }
    }
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
      currentDocument = null;
      currentExecuter = null;
      setStatus(documentID,  AnalysisStatus.CANCELLED);
      startNextAnalysis();
    } else {
      Iterator<Document> it = analysisQueue.iterator();
      while (it.hasNext()) {
        if (it.next().getId().equals(documentID)) {
          // Only set status to cancelled if it is currently running or scheduled
          setStatus(documentID,  AnalysisStatus.CANCELLED);
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
    EntityManager em = null;
    Document document;
    try {
      em = model.getEntityManager();
      TypedQuery<Document> query = em.createNamedQuery("Document.findDocumentById", Document.class);
      query.setParameter("documentId", documentId);
      List<Document> documents = query.getResultList();
      if (documents.isEmpty()) {
        throw new IllegalArgumentException("No such document found");
      }
      document = documents.get(0);
      AnalysisResetter.resetDocument(em, document);
    } finally {
      if (em != null) {
        em.close();
      }
    }

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

  private void setStatus(String documentId, AnalysisStatus status) {
    EntityManager em = null;
    try {
      em = model.getEntityManager();
      em.getTransaction().begin();
      TypedQuery<Document> query = em.createNamedQuery("Document.findDocumentById", Document.class);
      query.setParameter("documentId", documentId);
      List<Document> documents = query.getResultList();
      if (documents.isEmpty()) {
        throw new IllegalArgumentException("No such document found");
      }
      Document document = documents.get(0);

      document.getProgress().setStatus(status);

      em.merge(document);
      em.getTransaction().commit();
    } finally {
      if (em != null) {
        em.close();
      }
    }
  }
}
