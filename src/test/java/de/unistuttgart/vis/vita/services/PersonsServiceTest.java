package de.unistuttgart.vis.vita.services;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.PersonTestData;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.services.responses.PersonsResponse;

/**
 * Performs test on the PersonsService
 */
public class PersonsServiceTest extends ServiceTest {

  private String docId;
  private PersonTestData testData;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    
    EntityManager em = getModel().getEntityManager();
    
    testData = new PersonTestData();
    Person testPerson = testData.createTestPerson();
    Document testDoc = new DocumentTestData().createTestDocument(1);
    testDoc.getContent().getPersons().add(testPerson);
    
    docId = testDoc.getId();
    
    em.getTransaction().begin();
    em.persist(testPerson);
    em.persist(testDoc);
    em.getTransaction().commit();
    em.close();
  }
  
  @Override
  protected Application configure() {
    return new ResourceConfig(PersonsService.class);
  }
  
  /**
   * Tests whether persons can be caught using the REST interface.
   */
  @Test
  public void testGetPersons() {
    String path = "documents/" + docId + "/persons";
    PersonsResponse actualResponse = target(path).queryParam("offset", 0)
                                                  .queryParam("count", 10)
                                                  .request().get(PersonsResponse.class);
    assertEquals(1, actualResponse.getTotalCount());
    List<Person> persons = actualResponse.getPersons();
    assertEquals(1, persons.size());
    testData.checkData(persons.get(0));
  }

}
