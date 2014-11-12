package de.unistuttgart.vis.vita.services.entity;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.AttributeTestData;
import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.PersonTestData;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.services.ServiceTest;
import de.unistuttgart.vis.vita.services.responses.AttributesResponse;
import de.unistuttgart.vis.vita.services.responses.BasicAttribute;

/**
 * Performs test on the AttributesService.
 */
public class AttributesServiceTest extends ServiceTest {
  
  private AttributeTestData testData;
  
  private String docId;
  private String entityId;
  private String attributeId;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    
    EntityManager em = Model.createUnitTestModel().getEntityManager();
    
    // set up test data
    testData = new AttributeTestData();
    Document testDocument = new DocumentTestData().createTestDocument(1);
    Person testPerson = new PersonTestData().createTestPerson(1);
    testDocument.getContent().getPersons().add(testPerson);
    Attribute testAttribute = testData.createTestAttribute(1);
    testPerson.getAttributes().add(testAttribute);
    
    // save ids for later queries
    docId = testDocument.getId();
    entityId = testPerson.getId();
    attributeId = testAttribute.getId();
    
    // persist test data
    em.getTransaction().begin();
    em.persist(testAttribute);
    em.persist(testPerson);
    em.persist(testDocument);
    em.getTransaction().commit();
    em.close();
  }
  
  @Override
  protected Application configure() {
    return new ResourceConfig(AttributesService.class);
  }

  /**
   * Tests whether Attributes of an entity can be caught using the REST interface.
   */
  @Test
  public void testGetAttributes() {
    String path = "/documents/" + docId + "/entities/" + entityId + "/attributes";
    AttributesResponse actualResponse = target(path).queryParam("offset", 0)
                                                    .queryParam("count", 10)
                                                    .request().get(AttributesResponse.class);
    
    // check received data
    assertNotNull(actualResponse);
    assertEquals(1, actualResponse.getTotalCount());
    List<BasicAttribute> receivedAttributes = actualResponse.getAttributes();
    assertEquals(1, receivedAttributes.size());
    BasicAttribute receivedAttribute = receivedAttributes.get(0);
    assertEquals(attributeId, receivedAttribute.getId());
    testData.checkData(receivedAttribute);
  }

}
