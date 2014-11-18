package de.unistuttgart.vis.vita.services.entity;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.unistuttgart.vis.vita.model.entity.Person;

/**
 * Provides methods to GET a person with the current id.
 */
@ManagedBean
public class PersonService {
  
  private String personId;

  @Inject
  private EntityManager em;
  
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
    Person readPerson = null;
    
    try {
      readPerson = readPersonFromDatabase();
    } catch (NoResultException e) {
      throw new WebApplicationException(e, Response.status(Response.Status.NOT_FOUND).build());
    }
    
    return readPerson;
  }

  private Person readPersonFromDatabase() {
    TypedQuery<Person> query = em.createNamedQuery("Person.findPersonById", Person.class);
    query.setParameter("personId", personId);
    return query.getSingleResult();
  }

}
