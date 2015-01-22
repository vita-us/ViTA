package de.unistuttgart.vis.vita.services.entity;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import de.unistuttgart.vis.vita.analysis.AnalysisStatus;
import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.PersonTestData;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.services.AnalysisAware;
import de.unistuttgart.vis.vita.services.ServiceTest;
import de.unistuttgart.vis.vita.services.responses.PersonsResponse;

/**
 * Performs test on the PersonsService.
 */
public class PersonsServiceTest extends ServiceTest implements AnalysisAware {

  public static final int DEFAULT_OFFSET = 0;
  public static final int DEFAULT_COUNT = 10;

  public static final String PATH_PREFIX = "documents/";
  public static final String PATH_SUFFIX = "/persons";

  protected String docId;
  protected String path;

  private PersonTestData testData;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    
    EntityManager em = getModel().getEntityManager();
    
    testData = new PersonTestData();
    Person testPerson = testData.createTestPerson();
    Document testDoc = new DocumentTestData().createTestDocument(1);
    testDoc.getContent().getPersons().add(testPerson);
    testDoc.getProgress().setStatus(getCurrentAnalysisStatus());
    
    docId = testDoc.getId();

    path = PATH_PREFIX + docId + PATH_SUFFIX;
    
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

  public AnalysisStatus getCurrentAnalysisStatus() {
    return AnalysisStatus.FINISHED;
  }

  /**
   * Tests whether persons can be caught using the REST interface.
   */
  @Test
  public void testGetPersons() {
    PersonsResponse actualResponse = target(path).queryParam("offset", DEFAULT_OFFSET)
                                                  .queryParam("count", DEFAULT_COUNT)
                                                  .request().get(PersonsResponse.class);
    assertEquals(1, actualResponse.getTotalCount());
    List<Person> persons = actualResponse.getPersons();
    assertEquals(1, persons.size());
    testData.checkData(persons.get(0));
  }

}
