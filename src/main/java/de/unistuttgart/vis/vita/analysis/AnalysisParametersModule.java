package de.unistuttgart.vis.vita.analysis;

import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;

@AnalysisModule(weight = 0)
public class AnalysisParametersModule extends Module<AnalysisParameters> {
  private AnalysisParameters parameters;

  public AnalysisParametersModule(AnalysisParameters parameters) {
    this.parameters = parameters;
  }

  @Override
  public AnalysisParameters execute(ModuleResultProvider results, ProgressListener progressListener)
      throws Exception {
    return parameters;
  }
}
