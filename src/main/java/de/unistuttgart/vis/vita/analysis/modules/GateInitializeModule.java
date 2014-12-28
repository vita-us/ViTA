/*
 * GateInitializeModule.java
 *
 */

package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;

import java.io.File;
import java.net.URL;

import gate.Gate;

/**
 *
 */
@AnalysisModule
public class GateInitializeModule extends Module<GateInitializeModule> {

  @Override
  public GateInitializeModule execute(ModuleResultProvider results,
                                      ProgressListener progressListener) throws Exception {
    if (Gate.isInitialised()) {
      return this;
    }

    // Path to the gate_home resource
    URL pathToHome = this.getClass().getResource("/gate_home");
    File fileToHome = new File("");

    if (pathToHome != null) {
      fileToHome = new File(pathToHome.toURI());
    }

    File pluginsHome =
        new File(fileToHome.getAbsolutePath() + File.separator + "plugins" + File.separator);
    File siteConfig = new File(fileToHome.getAbsolutePath() + File.separator + "gate.xml");

    Gate.setGateHome(fileToHome);
    Gate.setPluginsHome(pluginsHome);
    Gate.setSiteConfigFile(siteConfig);
    Gate.init();

    return this;
  }
}
