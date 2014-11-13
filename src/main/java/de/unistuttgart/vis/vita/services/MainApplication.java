package de.unistuttgart.vis.vita.services;

import javax.persistence.EntityManager;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import de.unistuttgart.vis.vita.analysis.AnalysisController;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.services.document.DocumentsService;

public class MainApplication extends ResourceConfig {
  public MainApplication() {
    super(MultiPartFeature.class, DocumentsService.class);
    packages(true, "de.unistuttgart.vis.vita.services");
    register(new MainApplicationBinder());
  }
  
  private static class MainApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
      bind(Model.class).to(Model.class);
      bindFactory(Model.class).to(EntityManager.class);
      bind(AnalysisController.class).to(AnalysisController.class);
    }
  }
}
