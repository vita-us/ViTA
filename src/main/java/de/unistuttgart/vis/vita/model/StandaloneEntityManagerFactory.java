package de.unistuttgart.vis.vita.model;

import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * Represents the EntityManagerFactory being used in the StandaloneApplication.
 */
public class StandaloneEntityManagerFactory implements Factory<EntityManager> {

  @Inject
  private Model model;

  /**
   * @return an EntityManager provided by the model
   */
  @Override
  public EntityManager provide() {
    return model.provide();
  }

  /**
   * Disposes the given EntityManager.
   *
   * @param instance - the EntityManager instance which should be disposed
   */
  @Override
  public void dispose(EntityManager instance) {
    model.dispose(instance);
  }
}
