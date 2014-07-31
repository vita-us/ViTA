package de.unistuttgart.vis.vita.analysis;

import java.nio.file.Path;

/**
 *  The AnalysisController resolves the dependencies of every module. It also provides a optimized
 *  order corresponding to the computers cores.
 *
 *  The controller starts and cancels the analysis.
 */
public class AnalysisController {

  public AnalysisController(Model model) {

  }

  public AnalysisController(Model model, ModuleRegistry moduleRegistry) {

  }

  public String schedueDocumentAnalysis(Path filepath) {
    return null;
  }

  public void cancelAnalysis(String documentID) {

  }
}
