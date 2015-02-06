package de.unistuttgart.vis.vita.services.entity;

import javax.annotation.ManagedBean;
import javax.persistence.NoResultException;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.unistuttgart.vis.vita.model.dao.PersonDao;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.services.BaseService;

/**
 * Provides methods to GET a person with the current id.
 */
@ManagedBean
public class PersonService extends BaseService {
  private String personId;

  private PersonDao personDao;

  @Override public void postConstruct() {
    super.postConstruct();
    personDao = getDaoFactory().getPersonDao();
  }

  /**
   * Sets the id of the Person this resource should represent.
   * 
   * @param id the id
   */
  public PersonService setPersonId(String id) {
    this.personId = id;
    return this;
  }
  
  /**
   * Reads the requested person from the database and returns it in JSON using the REST.
   * 
   * @return the person with the current id in JSON
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Person getPerson() {
    Person readPerson;
    
    try {
      readPerson = personDao.findById(personId);
    } catch (NoResultException e) {
      throw new WebApplicationException(e, Response.status(Response.Status.NOT_FOUND).build());
    }
    
    return readPerson;
  }

}
