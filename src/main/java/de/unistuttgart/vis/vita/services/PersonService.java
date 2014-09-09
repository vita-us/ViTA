package de.unistuttgart.vis.vita.services;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.entity.Person;

/**
 * Provides methods to GET a person with the current id.
 */
@ManagedBean
public class PersonService {
  
  private String personId;

  @Context
  private ResourceContext resourceContext;
  
  private EntityManager em;
  
  @Inject
  public PersonService(Model model) {
    em = model.getEntityManager();
  }
  
  /**
   * Sets the id of the person this resource should represent
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
    return readPersonFromDatabase();
  }

  private Person readPersonFromDatabase() {
    System.out.println(personId);
    TypedQuery<Person> query = em.createNamedQuery("Person.findPersonById", Person.class);
    query.setParameter("personId", personId);
    return query.getSingleResult();
  }

}
