package de.unistuttgart.vis.vita.services;

import de.unistuttgart.vis.vita.services.document.DocumentsService;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.validation.ValidationFeature;

/**
 * Created by jan on 03.01.15.
 */
public class BaseApplication extends ResourceConfig {
    protected static final String SERVICES_PACKAGE = "de.unistuttgart.vis.vita.services";

    public BaseApplication() {
        packages(true, SERVICES_PACKAGE);
        register(MultiPartFeature.class);
        register(ValidationFeature.class);
        register(DocumentsService.class);
    }
}
