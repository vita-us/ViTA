package de.unistuttgart.vis.vita.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * The AnalysisController resolves the dependencies of every module. It also provides a optimized
 * order corresponding to the computers cores.
 *
 * The controller starts and cancels the analysis.
 */
public class AnalysisController {

  private Model model;
  private ModuleRegistry moduleRegistry;
  private List<Module<?>> moduleList;



  /**
   * New instance of the controller with given model. It will be created a new empty module
   * registry.
   * 
   * @param model The model with data.
   */
  public AnalysisController(Model model) {
    this.model = model;
    this.moduleRegistry = ModuleRegistry.getDefaultRegistry();
  }

  /**
   * New instance of the controller with given model and registry.
   * 
   * @param model The model to use.
   * @param moduleRegistry The registry to use.
   */
  public AnalysisController(Model model, ModuleRegistry moduleRegistry) {
    this.model = model;
    this.moduleRegistry = moduleRegistry;
  }

  /**
   * Starts the schedule of all modules registered in the registry. It calculates which modules can
   * be started first and which have to wait for other modules. This algorithm also checks how many
   * cores the CPU has and optimize it for multi-threading.
   * 
   * @param filepath The path to the document.
   * @return The name of the document
   */
  public String scheduleDocumentAnalysis(Path filepath) {
    return null;
  }

  /**
   * Starts the analyze for the document.
   * 
   * @param filepath The path to the document. TODO do we need this method?
   */
  public void startAnalysis(Path filepath) {

  }

  /**
   * Cancels the analysis if necessary. The process will be canceled as soon as possible.
   * 
   * @param documentID The document which is analyzed.
   */
  public void cancelAnalysis(String documentID) {

  }
}
