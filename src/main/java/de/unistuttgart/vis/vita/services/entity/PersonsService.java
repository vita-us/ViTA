package de.unistuttgart.vis.vita.services.entity;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.dao.PersonDao;
import de.unistuttgart.vis.vita.services.BaseService;
import de.unistuttgart.vis.vita.services.responses.PersonsResponse;

/**
 * Provides a method to GET all persons mentioned in the document this service refers to.
 */
@ManagedBean
public class PersonsService extends BaseService {

  private String documentId;

  private PersonDao personDao;

  @Inject
  private PersonService personService;

  @Override public void postConstruct() {
    super.postConstruct();
    personDao = getDaoFactory().getPersonDao();
  }

  /**
   * Sets the id of the document for which this service should provide the mentioned persons.
   * 
   * @param docId - the id of the document
   * @return the persons service
   */
  public PersonsService setDocumentId(String docId) {
    this.documentId = docId;
    return this;
  }

  /**
   * Returns a PersonsResponse including a list of Persons with a given maximum length, starting at
   * an also given offset.
   * 
   * @param offset - the first Person to be returned
   * @param count - the maximum amount of Persons to be returned
   * @return PersonsResponse including a list of Persons
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public PersonsResponse getPersons(@QueryParam("offset") int offset,
                                    @QueryParam("count") int count) {
    return new PersonsResponse(personDao.findInDocument(documentId, offset, count));
  }
  
  /**
   * Returns the Service to access the Person with the given id.
   * 
   * @param id - the id of the Person to be accessed
   * @return the PersonService to access the Person with the given id
   */
  @Path("{personId}")
  public PersonService getPerson(@PathParam("personId") String id) {
    return personService.setPersonId(id);
  }

}
