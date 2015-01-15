package de.unistuttgart.vis.vita.services;

import de.unistuttgart.vis.vita.services.document.DocumentsService;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.validation.ValidationFeature;

public class BaseApplication extends ResourceConfig {
    protected static final String SERVICES_PACKAGE = "de.unistuttgart.vis.vita.services";
    protected static final String DAO_PACKAGE = "de.unistuttgart.vis.vita.model.dao";
    protected static final String ANALYSIS_PACKAGE = "de.unistuttgart.vis.vita.analysis";
    
    public BaseApplication() {
        packages(true, SERVICES_PACKAGE);
        packages(true, DAO_PACKAGE);
        packages(true, ANALYSIS_PACKAGE);
        register(MultiPartFeature.class);
        register(ValidationFeature.class);
        register(DocumentsService.class);
    }
}
