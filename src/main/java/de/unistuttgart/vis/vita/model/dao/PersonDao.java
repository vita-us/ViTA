package de.unistuttgart.vis.vita.model.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.entity.Person;

/**
 * Represents a data access object for accessing Persons.
 */
public class PersonDao extends JpaDao<Person, String> {

  /**
   * Creates a new data access object for accessing Persons.
   */
  public PersonDao() {
    super(Person.class);
  }
  
  /**
   * Finds all Persons who occur in a given document.
   * 
   * @param docId - the id of the document to search in
   * @return list of all Persons occurring in the given document
   */
  public List<Person> findPersonsInDocument(String docId) {
    TypedQuery<Person> docQuery = em.createNamedQuery("Person.findPersonsInDocument", 
                                                        Person.class);
    docQuery.setParameter("docId", docId);
    return docQuery.getResultList();
  }
  
  /**
   * Finds the Person with the given name.
   * 
   * @param personName - the name of the Person to search for
   * @return the Person with the given name
   */
  public Person findPersonByName(String personName) {
    TypedQuery<Person> nameQuery = em.createNamedQuery("Person.findPersonForName", Person.class);
    nameQuery.setParameter("personName", personName);
    return nameQuery.getSingleResult();
  }

}
