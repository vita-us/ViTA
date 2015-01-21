/*
 * ANNIEModule.java
 *
 */

package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.results.AnnieDatastore;
import de.unistuttgart.vis.vita.analysis.results.AnnieNLPResult;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

import java.io.File;
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
import gate.Gate;
import gate.creole.ANNIEConstants;
import gate.creole.ConditionalSerialAnalyserController;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;

/**
 * Gate ANNIE module which searches for persons and locations.
 */
public class ANNIEModule extends Module<AnnieNLPResult> {
  private static final int PROGRESS_RESET_SPAN = 20;
  private ImportResult importResult;
  private DocumentPersistenceContext documentIdModule;
  private ProgressListener progressListener;
  private ConditionalSerialAnalyserController controller;
  private Map<Document, Chapter> docToChapter = new HashMap<>();
  private Map<Chapter, Set<Annotation>> chapterToAnnotation = new HashMap<>();
  private Corpus corpus;
  private int oldProgress = 0;
  private int documentsFinished = 0;
  private int maxDocuments;
  private double progressSteps;

  @Override
  public AnnieNLPResult execute(ModuleResultProvider result, ProgressListener progressListener)
      throws Exception {
    importResult = result.getResultFor(ImportResult.class);
    documentIdModule = result.getResultFor(DocumentPersistenceContext.class);
    AnnieDatastore storeModule = result.getResultFor(AnnieDatastore.class);
    this.progressListener = progressListener;
    String persistID = documentIdModule.getDocumentContentId();

    corpus = storeModule.getStoredAnalysis(persistID);

    // Persist the annie analysis into the datastore with the document id
    if (corpus == null) {
      loadAnnie();
      createCorpus();
      startAnnie();
      storeModule.storeResult(corpus, persistID);
    } else {
      fillCorpusMap();
    }

    createResultMap();

    progressListener.observeProgress(1);
    return buildResult();
  }

  private void fillCorpusMap() {
    int i = 0;

    for (DocumentPart part : importResult.getParts()) {
      for (Chapter chapter : part.getChapters()){
        docToChapter.put(corpus.get(i), chapter);
        i++;
      }
    }
  }

  private void createResultMap() {
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
   * Starts the real execution on the corpus with the annie controller.
   */
  private void startAnnie() throws ExecutionException {
    controller.setCorpus(corpus);

    controller.addProgressListener(new gate.event.ProgressListener() {

      @Override
      public void progressChanged(int i) {
        if (Thread.currentThread().isInterrupted()) {
          controller.interrupt();
        }
        
        calcProgress(i);
      }

      @Override
      public void processFinished() {
        // nothing to do
      }
    });

    controller.execute();
  }

  private void calcProgress(int i) {
    if (oldProgress - PROGRESS_RESET_SPAN > i) {
      documentsFinished++;
    }

    double finishFactor = (double) documentsFinished / (double) maxDocuments;
    double progChapt = progressSteps * ((double) i / 100);
    oldProgress = i;
    double currentProgress = finishFactor + progChapt;

    progressListener.observeProgress(currentProgress);
  }

  /**
   * Creates the Gate corpus out of the available chapters.
   */
  private void createCorpus() throws ResourceInstantiationException {
    corpus = Factory.newCorpus("ViTA Corpus");

    for (DocumentPart part : importResult.getParts()) {
      for (Chapter chapter : part.getChapters()) {
        Document doc = Factory.newDocument(chapter.getText());
        docToChapter.put(doc, chapter);
        corpus.add(doc);
      }
    }
    maxDocuments = corpus.size();
    if (maxDocuments > 0) {
      progressSteps = 1 / maxDocuments;
    } else {
      progressSteps = 0;
    }
  }

  /**
   * Initialize the ANNIE plugin for execution.
   */
  private void loadAnnie() throws GateException, IOException {
    if (controller != null) {
      return;
    }

    File pluginsHome = Gate.getPluginsHome();
    File anniePlugin = new File(pluginsHome, ANNIEConstants.PLUGIN_DIR);
    File annieGapp = new File(anniePlugin, ANNIEConstants.DEFAULT_FILE);

    controller =
        (ConditionalSerialAnalyserController) PersistenceManager.loadObjectFromFile(annieGapp);
  }

  /**
   * @return The result with a set of annotations.
   */
  private AnnieNLPResult buildResult() {
    return new AnnieNLPResult() {
      @Override
      public Set<Annotation> getAnnotationsForChapter(Chapter chapter) {
        if (!chapterToAnnotation.containsKey(chapter)) {
          throw new IllegalArgumentException("This chapter has not been analyzed");
        }

        return chapterToAnnotation.get(chapter);
      }
    };
  }
}
