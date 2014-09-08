package de.unistuttgart.vis.vita.services;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.services.responses.PersonsResponse;

/**
 * Provides a method to GET all persons mentioned in the document it refers to.
 */
@ManagedBean
public class PersonsService {

  private String documentId;

  private EntityManager em;

  /**
   * Creates a new Instance of PersonsService.
   */
  @Inject
  public PersonsService(Model model) {
    em = model.getEntityManager();
  }

  /**
   * Sets the id of the document of which this service should provide the mentioned persons.
   * 
   * @param docId - the id of the document
   * @return
   */
  public PersonsService setDocumentId(String docId) {
    this.documentId = docId;
    return this;
  }

  /**
   * Returns a PersonsResponse including a list of Persons with a given maximum length,
   * starting at an also given offset.
   * 
   * @param offset - the first Person to be returned
   * @param count - the maximum amount of Persons to be returned
   * @return PersonsResponse including a list of Persons
   */
  @GET
  public PersonsResponse getPersons(@QueryParam("offset") int offset,
                                    @QueryParam("count") int count) {
    return readPersonsFromDatabase(offset, count);
  }

  private PersonsResponse readPersonsFromDatabase(int offset, int count) {
    TypedQuery<Person> query = em.createNamedQuery("Person.findPersonsInDocument", Person.class);
    query.setParameter("documentId", documentId);
    
    query.setFirstResult(offset);
    query.setMaxResults(count);
    
    return new PersonsResponse(query.getResultList());
  }

}
