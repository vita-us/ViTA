package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.TextRepository;

@AnalysisModule(dependencies = Model.class)
public class LuceneModule implements Module<TextRepository> {

  @Override
  public void observeProgress(double progress) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public TextRepository execute(ModuleResultProvider result, ProgressListener progressListener)
      throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

}
