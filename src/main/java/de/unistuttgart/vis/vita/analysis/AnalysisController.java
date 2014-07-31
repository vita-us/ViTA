package de.unistuttgart.vis.vita.analysis;

import java.nio.file.Path;

/**
 *  The AnalysisController resolves the dependencies of every module. It also provides a optimized
 *  order corresponding to the computers cores.
 *
 *  The controller starts and cancels the analysis.
 */
public class AnalysisController {

  private Model model;
  private ModuleRegistry moduleRegistry;

  public AnalysisController(Model model) {
    this.model = model;
  }

  public AnalysisController(Model model, ModuleRegistry moduleRegistry) {
    this.model = model;
    this.moduleRegistry = moduleRegistry;
    
    
    
  }

  public String scheduleDocumentAnalysis(Path filepath) {
    
    return null;
  }
  
  public void startAnalysis(Path filepath) {
    
  }

  public void cancelAnalysis(String documentID) {
    
  }
}
