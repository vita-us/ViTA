package de.unistuttgart.vis.vita.model;

import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class StandaloneEntityManagerFactory implements Factory<EntityManager> {
	@Inject
	private Model model;

	@Override public EntityManager provide() {
		return model.provide();
	}

	@Override public void dispose(EntityManager instance) {
		model.dispose(instance);
	}
}
