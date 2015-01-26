/*
 * StanfordNLPModule.java
 *
 */

package de.unistuttgart.vis.vita.analysis.modules.gate;

import de.unistuttgart.vis.vita.analysis.results.StanfordNLPResult;
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
import gate.creole.ConditionalSerialAnalyserController;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;

/**
 *
 */
public class StanfordNLPModule extends AbstractNLPModule<StanfordNLPResult> {

  public static final String PLUGIN_DIR = "Stanford_CoreNLP";
  public static final String DEFAULT_FILE = "stanford_ner_state.xgapp";

  @Override
  protected void loadEngine() throws GateException, IOException {
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

  @Override
  protected StanfordNLPResult buildResult() {
    return new StanfordNLPResult() {
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

  /**
   * Need to be overriden because stanford has slightly different annotation types for persons and
   * locations.
   */
  @Override
  protected void createResultMap() {
    for (Object docObj : corpus) {
      Document doc = (Document) docObj;
      AnnotationSet defaultAnnotSet = doc.getAnnotations();
      Set<String> annotTypesRequired = new HashSet<>();
      annotTypesRequired.add(NLPConstants.TYPE_PERSON_STANFORD);
      annotTypesRequired.add(NLPConstants.TYPE_LOCATION_STANFORD);
      Set<Annotation> peopleAndPlaces = new HashSet<>(defaultAnnotSet.get(annotTypesRequired));
      chapterToAnnotation.put(docToChapter.get(doc), peopleAndPlaces);
    }
  }
}
