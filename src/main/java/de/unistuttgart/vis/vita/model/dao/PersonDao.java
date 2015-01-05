package de.unistuttgart.vis.vita.model.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NamedQuery;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.entity.Person;

/**
 * Represents a data access object for accessing Persons.
 */
@Stateless
public class PersonDao extends JpaDao<Person, String> {

  /**
   * Creates a new data access object for accessing Persons.
   */
  public PersonDao() {
    super(Person.class);
  }

  public List<Person> findInDocument(String documentId, int offset, int count) {
    TypedQuery<Person> docQuery = em.createNamedQuery(getInDocumentQueryName(), Person.class);
    docQuery.setParameter("documentId", documentId);
    return docQuery.getResultList();
  }

  /**
   * @return the name of the {@link NamedQuery} for searching in a specific Document
   */
  public String getInDocumentQueryName() {
    String className = getPersistentClassName();
    return className + "." + "find" + className + "s" + "InDocument";
  }

}
