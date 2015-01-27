/*
 * AbstractNLPModule.java
 *
 */

package de.unistuttgart.vis.vita.analysis.modules.gate;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.results.AnnieDatastore;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.analysis.results.NLPResult;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.creole.ANNIEConstants;
import gate.creole.ConditionalSerialAnalyserController;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;

/**
 * Abstract module for nlp tools. Implements the most common needed methods for the analysis.
 * Can be overwritten if needed.
 */
public abstract class AbstractNLPModule<T extends NLPResult> extends Module<T> {

  private final String CORPUS_NAME = "ViTA Corpus";
  protected ImportResult importResult;
  protected ProgressListener progressListener;
  protected AnalysisParameters parameters;
  protected ConditionalSerialAnalyserController controller;
  protected Map<Document, Chapter> docToChapter = new HashMap<>();
  protected Map<Chapter, Document> chapterToDoc = new HashMap<>();
  protected Map<Chapter, Set<Annotation>> chapterToAnnotation = new HashMap<>();
  protected Corpus corpus;
  protected DocumentPersistenceContext documentIdModule;
  protected AnnieDatastore storeModule;

  @Override
  public T execute(ModuleResultProvider results, ProgressListener progressListener)
      throws Exception {
    importResult = results.getResultFor(ImportResult.class);
    documentIdModule = results.getResultFor(DocumentPersistenceContext.class);
    storeModule = results.getResultFor(AnnieDatastore.class);
    parameters = results.getResultFor(AnalysisParameters.class);
    this.progressListener = progressListener;
    String persistID = documentIdModule.getDocumentContentId() + "-"
                       + parameters.getNlpTool().getName();

    corpus = storeModule.getStoredAnalysis(persistID);

    // Persist the analysis into the datastore with the document id
    if (corpus == null) {
      loadEngine();
      createCorpus();
      startAnalysis();
      storeModule.storeResult(corpus, persistID);
    } else {
      fillCorpusMap();
    }

    createResultMap();

    progressListener.observeProgress(1);
    return buildResult();
  }

  /**
   * Refills the needed map from the datastore for the result.
   */
  protected void fillCorpusMap() {
    int i = 0;

    for (DocumentPart part : importResult.getParts()) {
      for (Chapter chapter : part.getChapters()) {
        docToChapter.put(corpus.get(i), chapter);
        i++;
      }
    }
  }

  /**
   * Creates the Gate corpus out of the available chapters.
   */
  protected void createCorpus() throws ResourceInstantiationException {
    corpus = Factory.newCorpus(CORPUS_NAME);

    for (DocumentPart part : importResult.getParts()) {
      for (Chapter chapter : part.getChapters()) {
        Document doc = Factory.newDocument(chapter.getText());
        docToChapter.put(doc, chapter);
        chapterToDoc.put(chapter, doc);
        corpus.add(doc);
      }
    }
  }

  /**
   * Creates the result mapping.
   */
  protected void createResultMap() {

    for (Object docObj : corpus) {
      Document doc = (Document) docObj;
      AnnotationSet defaultAnnotSet = doc.getAnnotations();
      Set<String> annotTypesRequired = new HashSet<>();
      annotTypesRequired.add(ANNIEConstants.PERSON_ANNOTATION_TYPE);
      annotTypesRequired.add(ANNIEConstants.LOCATION_ANNOTATION_TYPE);
      Set<Annotation> peopleAndPlaces = new HashSet<>(defaultAnnotSet.get(annotTypesRequired));
      chapterToAnnotation.put(docToChapter.get(doc), peopleAndPlaces);
    }
  }

  /**
   * Starts the analysis with the initialized controller.
   *
   * @throws ExecutionException If an exception occurs during execution of the controller.
   */
  protected void startAnalysis() throws ExecutionException {
    controller.setCorpus(corpus);

    int maxDocuments = corpus.size();

    controller.addProgressListener(new GateControllerProgress(progressListener, maxDocuments));

    try {
      controller.execute();
    } catch (IllegalStateException e) {
      // This needs testing somehow. TODO
      System.out.println(e.getCause().toString());
      controller.interrupt();
    }
  }

  protected abstract void loadEngine() throws GateException, IOException;

  /**
   * @return The result with a set of annotations.
   */
  protected abstract T buildResult();
}
