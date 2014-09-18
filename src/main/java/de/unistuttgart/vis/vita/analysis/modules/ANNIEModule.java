/*
 * ANNIEModule.java
 *
 */

package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.AnnieNLPResult;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
@AnalysisModule(dependencies = {ImportResult.class})
public class ANNIEModule implements Module<AnnieNLPResult> {

  private static final double GATE_LOAD_PROGRESS_FRACTION = 0.1;
  private static final double ANNIE_LOAD_PROGRESS_FRACTION = 0.2;
  private static final int progressResetSpan = 20;
  private ImportResult importResult;
  private ProgressListener progressListener;
  private ConditionalSerialAnalyserController controller;
  private Map<Document, Chapter> docToChapter = new HashMap<>();
  private Map<Chapter, Set<Annotation>> chapterToAnnotation = new HashMap<>();
  private Corpus corpus;
  private int old_progress = 0;
  private int documentsFinished = 0;
  private int maxDocuments;
  private double progressSteps;

  @Override
  public void observeProgress(double progress) {

  }

  @Override
  public AnnieNLPResult execute(ModuleResultProvider result, ProgressListener progressListener)
      throws Exception {
    importResult = result.getResultFor(ImportResult.class);
    this.progressListener = progressListener;
    initializeGate();
    loadAnnie();
    createCorpus();
    startAnnie();
    progressListener.observeProgress(1);
    return buildResult();
  }

  /**
   * Starts the real execution on the corpus with the annie controller.
   */
  private void startAnnie() throws ExecutionException {
    controller.setCorpus(corpus);

    controller.addProgressListener(new gate.event.ProgressListener() {

      @Override
      public void progressChanged(int i) {
        calcProgress(i);
      }

      @Override
      public void processFinished() {

      }
    });

    controller.execute();

    for (Object docObj : corpus) {
      Document doc = (Document) docObj;
      AnnotationSet defaultAnnotSet = doc.getAnnotations();
      Set annotTypesRequired = new HashSet();
      annotTypesRequired.add(ANNIEConstants.PERSON_ANNOTATION_TYPE);
      annotTypesRequired.add(ANNIEConstants.LOCATION_ANNOTATION_TYPE);
      Set<Annotation> peopleAndPlaces = new HashSet<>(defaultAnnotSet.get(annotTypesRequired));
      chapterToAnnotation.put(docToChapter.get(doc), peopleAndPlaces);
    }
  }

  private void calcProgress(int i) {
    if (old_progress - progressResetSpan > i) {
      documentsFinished++;
    }

    double finishFactor = (double) documentsFinished / (double) maxDocuments;
    double progDocs = (1 - ANNIE_LOAD_PROGRESS_FRACTION) * finishFactor;
    double progChapt = progressSteps * ((double) i / 100);
    old_progress = i;
    double currentProgress = ANNIE_LOAD_PROGRESS_FRACTION + progDocs + progChapt;

    progressListener.observeProgress(currentProgress);
  }

  /**
   * Creates the Gate corpus out of the available chapters.
   */
  private void createCorpus() throws ResourceInstantiationException, ExecutionException {
    corpus = Factory.newCorpus("ViTA Corpus");

    for (DocumentPart part : importResult.getParts()) {
      for (Chapter chapter : part.getChapters()) {
        Document doc = Factory.newDocument(chapter.getText());
        docToChapter.put(doc, chapter);
        corpus.add(doc);
      }
    }
    maxDocuments = corpus.size();
    progressSteps = (1 - ANNIE_LOAD_PROGRESS_FRACTION) / maxDocuments;
  }

  /**
   * Initialize the Gate library once.
   */
  private void initializeGate() throws GateException {
    if (Gate.isInitialised()) {
      progressListener.observeProgress(GATE_LOAD_PROGRESS_FRACTION);
      return;
    }

    // Path to the gate_home resource
    URL pathToHome = this.getClass().getResource("/gate_home");
    File fileToHome = new File("");

    if (pathToHome != null) {
      fileToHome = new File(pathToHome.getFile());
    }

    File pluginsHome = new File(fileToHome.getAbsolutePath() + "\\plugins\\");
    File siteConfig = new File(fileToHome.getAbsolutePath() + "\\gate.xml");

    Gate.setGateHome(fileToHome);
    Gate.setPluginsHome(pluginsHome);
    Gate.setSiteConfigFile(siteConfig);
    Gate.init();
    progressListener.observeProgress(GATE_LOAD_PROGRESS_FRACTION);
  }

  /**
   * Initialize the ANNIE plugin for execution.
   */
  private void loadAnnie() throws GateException, IOException {
    if (controller != null) {
      progressListener.observeProgress(ANNIE_LOAD_PROGRESS_FRACTION);
      return;
    }

    File pluginsHome = Gate.getPluginsHome();
    File anniePlugin = new File(pluginsHome, ANNIEConstants.PLUGIN_DIR);
    File annieGapp = new File(anniePlugin, ANNIEConstants.DEFAULT_FILE);

    controller =
        (ConditionalSerialAnalyserController) PersistenceManager.loadObjectFromFile(annieGapp);

    progressListener.observeProgress(ANNIE_LOAD_PROGRESS_FRACTION);
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

      @Override
      public Document getDocumentForChapter(Chapter chapter) {
        return null;
      }
    };
  }
}
