package de.unistuttgart.vis.vita.services;

import de.unistuttgart.vis.vita.analysis.AnalysisStatus;

public interface AnalysisAware {

  AnalysisStatus getCurrentAnalysisStatus();

}
