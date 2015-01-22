package de.unistuttgart.vis.vita.model.entity;

import javax.persistence.DiscriminatorValue;

/**
 * Represents a character in a document.
 */
@javax.persistence.Entity
@DiscriminatorValue(Person.DISCRIMINATOR_VALUE)
public class Person extends Entity {
  
  // constants
  public static final String DISCRIMINATOR_VALUE= "Person";

  @Override
  public EntityType getType() {
    return EntityType.PERSON;
  }

}
