package de.unistuttgart.vis.vita.services;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.PersonTestData;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.entity.Person;

/**
 * Performs test on PersonService to check whether GET works correctly.
 */
public class PersonServiceTest extends ServiceTest {
  
  private String docId;
  private String personId;
  private PersonTestData testData;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    
    EntityManager em = Model.createUnitTestModel().getEntityManager();
    
    // set up test data
    testData = new PersonTestData();
    Person testPerson = testData.createTestPerson(1);
    Document testDoc = new DocumentTestData().createTestDocument(1);
    testDoc.getContent().getPersons().add(testPerson);
    
    // save ids for request
    docId = testDoc.getId();
    personId = testPerson.getId();
    
    // persist test data
    em.getTransaction().begin();
    em.persist(testPerson);
    em.persist(testDoc);
    em.getTransaction().commit();
    em.close();
  }
  
  @Override
  protected Application configure() {
    return new ResourceConfig(PersonService.class);
  }
  
  /**
   * Checks whether a Person can be got using REST.
   */
  @Test
  public void testGetPerson() {
    String path = "documents/" + docId + "/persons/" + personId;
    Person responsePerson = target(path).request().get(Person.class);
    
    testData.checkData(responsePerson, 1);
  }

}
