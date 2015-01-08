package de.unistuttgart.vis.vita.analysis;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.dao.DocumentDao;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;
import de.unistuttgart.vis.vita.model.document.DocumentMetrics;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.EntityRelation;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.model.entity.Place;
import de.unistuttgart.vis.vita.model.progress.AnalysisProgress;

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

  @Inject
  private DocumentDao documentDao;

  @Inject
  private AnalysisResetter analysisResetter;
  
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

  /**
   * Resets all documents which haven't been analysed because program didn't terminate properly.
   * Automatically restarts the analysis.
   */
  private void resetInterruptedDocuments() {
    List<Document> documents = documentDao.findDocumentsByStatus(AnalysisStatus.RUNNING);
    for (Document document : documents) {
      analysisResetter.resetAndFail(document);
    }
  }

  public AnalysisController(Model model, AnalysisExecutorFactory executorFactory) {
    this.model = model;
    this.executorFactory = executorFactory;
    resetInterruptedDocuments();
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
  public synchronized String scheduleDocumentAnalysis(Path filePath, String fileName) {
    Document document = createDocument(filePath, fileName);
    scheduleDocumentAnalyisis(document);
    return document.getId();
  }

  public synchronized String scheduleDocumentAnalysis(Document document) {
    persistDocument(document);
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
    currentExecuter = executorFactory.createExecutor(document);
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
        setStatus(document.getId(), AnalysisStatus.FAILED);
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

    persistDocument(document);

    return document;
  }

  private void persistDocument(Document document) {
    documentDao.save(document);
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
    Document document = documentDao.findById(documentId);
    analysisResetter.resetDocument(document);
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
    Document document = documentDao.findById(documentId);
    document.getProgress().setStatus(status);
    documentDao.save(document);
  }
}
