package de.unistuttgart.vis.vita.model.entity;

import javax.persistence.DiscriminatorValue;

/**
 * Represents a place in a document.
 */
@javax.persistence.Entity
@DiscriminatorValue(Place.DISCRIMINATOR_VALUE)
public class Place extends Entity {

  public static final String DISCRIMINATOR_VALUE = "Place";

  @Override
  public EntityType getType() {
    return EntityType.PLACE;
  }
  
}
