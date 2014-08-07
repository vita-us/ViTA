package de.unistuttgart.vis.vita.analysis.results;

import de.unistuttgart.vis.vita.model.entity.Entity;

/**
 * The result of calculating the fingerprint of entities
 */
public interface FingerprintResult {

  /**
   * Gets the fingerprint of the given entity.
   *
   * Each item in the array represents a fixed text span of equal length. An item of the array is
   * set if the entity occurs at the given spot in the text.
   *
   * @param entity the entity
   * @return the fingerprint
   */
  public boolean[] getFingerpintForEntity(Entity entity);
}
