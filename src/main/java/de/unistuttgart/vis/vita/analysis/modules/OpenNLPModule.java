/*
 * OpenNLPModule.java
 *
 */

package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.modules.gate.GateControllerProgress;
import de.unistuttgart.vis.vita.analysis.results.AnnieDatastore;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.analysis.results.OpenNLPResult;
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
import gate.creole.ConditionalSerialAnalyserController;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;

/**
 * OpenNLP module to analyse the text. Needs ANNIE as dependency because it shouldn't work parallel
 */
@AnalysisModule(dependencies = {GateInitializeModule.class, AnnieDatastore.class,
                                DocumentPersistenceContext.class,
                                ImportResult.class}, weight = 500)
public class OpenNLPModule extends Module<OpenNLPResult> {

  public static final String PLUGIN_DIR = "OpenNLP";
  public static final String DEFAULT_FILE = "opennlp_state.xgapp";
  private static final String ANNOTATION_TYPE_PERSON = "Person";
  private static final String ANNOTATION_TYPE_ORGANIZATION = "Organization";
  private static final String ANNOTATION_TYPE_LOCATION = "Location";

  private ImportResult importResult;
  private DocumentPersistenceContext documentIdModule;
  private ProgressListener progressListener;
  private ConditionalSerialAnalyserController controller;
  private Map<Document, Chapter> docToChapter = new HashMap<>();
  private Map<Chapter, Set<Annotation>> chapterToAnnotation = new HashMap<>();
  private Corpus corpus;

  @Override
  public OpenNLPResult execute(ModuleResultProvider results, ProgressListener progressListener)
      throws Exception {
    this.progressListener = progressListener;
    importResult = results.getResultFor(ImportResult.class);
    documentIdModule = results.getResultFor(DocumentPersistenceContext.class);
    AnnieDatastore storeModule = results.getResultFor(AnnieDatastore.class);
    String documentName = documentIdModule.getFileName();

    corpus = storeModule.getStoredAnalysis(documentName);

    // Persist the annie analysis into the datastore with the document id
    if (corpus == null) {
      loadEngine();
      createCorpus();
      startAnalysis();
      storeModule.storeResult(corpus, documentName);
    }

    createResultMap();
    progressListener.observeProgress(1);

    return buildResult();
  }

  private OpenNLPResult buildResult() {
    return new OpenNLPResult() {
      @Override
      public Set<Annotation> getAnnotationsForChapter(Chapter chapter) {
        if (!chapterToAnnotation.containsKey(chapter)) {
          throw new IllegalArgumentException("This chapter has not been analyzed");
        }

        return chapterToAnnotation.get(chapter);
      }
    };
  }

  private void createResultMap() {
    for (Object docObj : corpus) {
      Document doc = (Document) docObj;
      AnnotationSet defaultAnnotSet = doc.getAnnotations();
      Set<String> annotTypesRequired = new HashSet<>();
      annotTypesRequired.add(ANNOTATION_TYPE_LOCATION);
      Set<Annotation> peopleAndPlaces = new HashSet<>(defaultAnnotSet.get(annotTypesRequired));
      chapterToAnnotation.put(docToChapter.get(doc), peopleAndPlaces);
    }
  }

  private void startAnalysis() throws ExecutionException {
    controller.setCorpus(corpus);

    int maxDocuments = corpus.size();
    int progressSteps;

    if (maxDocuments > 0) {
      progressSteps = 1 / maxDocuments;
    } else {
      progressSteps = 0;
    }

    controller.addProgressListener(
        new GateControllerProgress(progressListener, maxDocuments, progressSteps));

    try {
      controller.execute();
    } catch (IllegalStateException e) {
      System.out.println(e.getCause().toString());
    }
  }

  private void loadEngine() throws GateException, IOException {
    if (controller != null) {
      return;
    }

    File pluginsHome = Gate.getPluginsHome();
    File engineFolder = new File(pluginsHome, PLUGIN_DIR);
    File engineState = new File(engineFolder, DEFAULT_FILE);
    Gate.getCreoleRegister().registerDirectories(engineFolder.toURI().toURL());

    controller =
        (ConditionalSerialAnalyserController) PersistenceManager.loadObjectFromFile(engineState);
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
  }
}
