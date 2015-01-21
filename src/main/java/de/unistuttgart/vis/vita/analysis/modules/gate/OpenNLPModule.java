/*
 * OpenNLPModule.java
 *
 */

package de.unistuttgart.vis.vita.analysis.modules.gate;

import de.unistuttgart.vis.vita.analysis.results.OpenNLPResult;
import de.unistuttgart.vis.vita.model.document.Chapter;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import gate.Annotation;
import gate.Gate;
import gate.creole.ConditionalSerialAnalyserController;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;

/**
 * OpenNLP module to analyse the text. Needs ANNIE as dependency because it shouldn't work parallel
 */
public class OpenNLPModule extends AbstractNLPModule<OpenNLPResult> {
  public static final String PLUGIN_DIR = "OpenNLP";
  public static final String DEFAULT_FILE = "opennlp_state.xgapp";

  @Override
  protected void loadEngine()
      throws GateException, IOException {
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
  protected OpenNLPResult buildResult() {
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
}
