package de.unistuttgart.vis.vita.services;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import de.unistuttgart.vis.vita.model.Model;

public class EntityService {
	private String entityId;

	private EntityManager em;

	@Inject
	public EntityService(Model model) {
		em = model.getEntityManager();
	}

	public EntityService setEntityId(String id) {
		this.entityId = id;
		return this;
	}

}
