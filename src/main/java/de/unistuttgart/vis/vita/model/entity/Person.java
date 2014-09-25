package de.unistuttgart.vis.vita.model.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Represents a character in a document.
 */
@javax.persistence.Entity
@DiscriminatorValue(Person.DISCRIMINATOR_VALUE)
@NamedQueries({
  @NamedQuery(name = "Person.findAllPersons",
      query = "SELECT ps "
      + "FROM Person ps"),
      
  @NamedQuery(name = "Person.findPersonById",
      query = "SELECT ps "
      + "FROM Person ps "
      + "WHERE ps.id = :personId"),
  
  @NamedQuery(name = "Person.findPersonByName",
      query = "SELECT ps "
      + "FROM Person ps "
      + "WHERE ps.displayName = :personName")
})
public class Person extends Entity {
  
  // constants
  public static final String DISCRIMINATOR_VALUE= "Person";

  @Override
  public EntityType getType() {
    return EntityType.PERSON;
  }

}
