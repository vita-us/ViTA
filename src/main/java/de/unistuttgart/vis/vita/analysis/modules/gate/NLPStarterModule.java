/*
 * NLPStarterModule.java
 *
 */

package de.unistuttgart.vis.vita.analysis.modules.gate;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.AnnieDatastore;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.analysis.results.NLPResult;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;

/**
 * Module which starts the correct nlp module depending on the setted parameter in the parameters.
 */
@AnalysisModule(dependencies = {GateInitializeModule.class, AnnieDatastore.class,
                                DocumentPersistenceContext.class, ImportResult.class,
                                AnalysisParameters.class}, weight = 500)
public class NLPStarterModule extends Module<NLPResult> {

  @Override
  public NLPResult execute(ModuleResultProvider results, ProgressListener progressListener)
      throws Exception {
    AnalysisParameters parameters = results.getResultFor(AnalysisParameters.class);
    AbstractNLPModule module;

    int a = 5;

    switch (a) {
      case 0:
        module = new ANNIEModule();
        break;
      case 1:
        module = new StanfordNLPModule();
        break;
      case 2:
        module = new OpenNLPModule();
        break;
      default:
        throw new IllegalStateException("Not registered nlp module " + a + "!");
    }

    return module.execute(results, progressListener);
  }
}
