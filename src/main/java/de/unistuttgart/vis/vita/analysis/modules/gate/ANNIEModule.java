/*
 * ANNIEModule.java
 *
 */

package de.unistuttgart.vis.vita.analysis.modules.gate;

import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.AnnieDatastore;
import de.unistuttgart.vis.vita.analysis.results.AnnieNLPResult;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.model.document.Chapter;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Gate;
import gate.creole.ANNIEConstants;
import gate.creole.ConditionalSerialAnalyserController;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.persistence.PersistenceManager;

/**
 * Gate ANNIE module which searches for persons and locations.
 */
@AnalysisModule(dependencies = {GateInitializeModule.class, AnnieDatastore.class,
                                DocumentPersistenceContext.class, ImportResult.class}, weight = 500)
public class ANNIEModule extends AbstractNLPModule<AnnieNLPResult> {

  /**
   * Initialize the ANNIE plugin for execution.
   */
  @Override
  protected void loadEngine()
      throws ResourceInstantiationException, IOException, PersistenceException {
    if (controller != null) {
      return;
    }

    File pluginsHome = Gate.getPluginsHome();
    File anniePlugin = new File(pluginsHome, ANNIEConstants.PLUGIN_DIR);
    File annieGapp = new File(anniePlugin, ANNIEConstants.DEFAULT_FILE);

    controller =
        (ConditionalSerialAnalyserController) PersistenceManager.loadObjectFromFile(annieGapp);
  }

  @Override
  protected AnnieNLPResult buildResult() {
    return new AnnieNLPResult() {
      @Override
      public Set<Annotation> getAnnotationsForChapter(Chapter chapter) {
        if (!chapterToAnnotation.containsKey(chapter)) {
          throw new IllegalArgumentException("This chapter has not been analyzed");
        }

        return chapterToAnnotation.get(chapter);
      }

      @Override
      public Set<Annotation> getAnnotationsForChapter(Chapter chapter,
                                                      Collection<String> type) {
        if (!chapterToAnnotation.containsKey(chapter)) {
          throw new IllegalArgumentException("This chapter has not been analyzed");
        }

        Document document = chapterToDoc.get(chapter);

        AnnotationSet defaultAnnotSet = document.getAnnotations();
        Set<String> annotTypesRequired = new HashSet<>();

        for (String s : type) {
          annotTypesRequired.add(s);
        }

        return new HashSet<>(defaultAnnotSet.get(annotTypesRequired));
      }
    };
  }

}
