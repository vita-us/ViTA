package de.unistuttgart.vis.vita.analysis;

import de.unistuttgart.vis.vita.analysis.modules.DocumentPersistenceContextModule;
import de.unistuttgart.vis.vita.analysis.modules.ImportModule;
import de.unistuttgart.vis.vita.analysis.modules.MainAnalysisModule;
import de.unistuttgart.vis.vita.analysis.modules.ModelProviderModule;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;

/**
 * Default implementation of {@link AnalysisExecutorFactory}, using a {@link AnalysisScheduler}
 */
public class DefaultAnalysisExecutorFactory implements AnalysisExecutorFactory {
  private static final Class<?> TARGET_MODULE = MainAnalysisModule.class;
  private Model model;
  private ModuleRegistry moduleRegistry;

  public DefaultAnalysisExecutorFactory(Model model, ModuleRegistry moduleRegistry) {
    this.model = model;
    this.moduleRegistry = moduleRegistry;
  }

  @Override
  public AnalysisExecutor createExecutor(Document document,
      AnalysisParameters parameters) {
    ImportModule importModule = new ImportModule(document.getFilePath());
    ModelProviderModule modelModule = new ModelProviderModule(model);
    DocumentPersistenceContextModule documentPersistenceContextModule =
        new DocumentPersistenceContextModule(document);
    AnalysisParametersModule analysisParametersModule = new AnalysisParametersModule(parameters);
    AnalysisScheduler scheduler = new AnalysisScheduler(moduleRegistry, ModuleClass.get(TARGET_MODULE),
        importModule, modelModule, documentPersistenceContextModule, analysisParametersModule);
    return new AnalysisExecutor(scheduler.getScheduledModules());
  }
}
