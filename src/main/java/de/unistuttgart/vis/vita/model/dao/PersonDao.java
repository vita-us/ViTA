package de.unistuttgart.vis.vita.model.dao;

import de.unistuttgart.vis.vita.model.entity.Person;

/**
 * Represents a data access object for accessing Persons.
 */
public class PersonDao extends EntityDao<Person> {

  /**
   * Creates a new data access object for accessing Persons.
   */
  public PersonDao() {
    super(Person.class);
  }

}
