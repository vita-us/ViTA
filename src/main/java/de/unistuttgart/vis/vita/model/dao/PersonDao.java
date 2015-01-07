package de.unistuttgart.vis.vita.model.dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.entity.Person;

/**
 * Represents a data access object for accessing Persons.
 */
@ManagedBean
@MappedSuperclass
@NamedQueries({
  @NamedQuery(name = "Person.findAllPersons",
              query = "SELECT ps "
                    + "FROM Person ps"),
      
  @NamedQuery(name = "Person.findPersonsInDocument",
              query = "SELECT ps "
                    + "FROM Person ps, Document d "
                    + "WHERE d.id = :documentId "
                    + "AND ps MEMBER OF d.content.persons "
                    + "ORDER BY ps.rankingValue"),
      
  @NamedQuery(name = "Person.findPersonById",
      query = "SELECT ps "
      + "FROM Person ps "
      + "WHERE ps.id = :personId"),
  
  @NamedQuery(name = "Person.findPersonByName",
      query = "SELECT ps "
      + "FROM Person ps "
      + "WHERE ps.displayName = :personName")
})
public class PersonDao extends JpaDao<Person, String> {

  private static final String DOCUMENT_ID_PARAMETER = "documentId";

  /**
   * Creates a new data access object for accessing Persons.
   */
  public PersonDao() {
    super(Person.class);
  }

  public List<Person> findInDocument(String documentId, int offset, int count) {
    TypedQuery<Person> docQuery = em.createNamedQuery(getInDocumentQueryName(), Person.class);
    docQuery.setParameter(DOCUMENT_ID_PARAMETER, documentId);
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
