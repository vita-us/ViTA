package de.unistuttgart.vis.vita.services.entity;

import static org.junit.Assert.*;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.AttributeTestData;
import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.EntityRelationTestData;
import de.unistuttgart.vis.vita.data.PersonTestData;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.EntityRelation;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.services.ServiceTest;
import de.unistuttgart.vis.vita.services.entity.PersonService;

/**
 * Performs test on PersonService to check whether GET works correctly.
 */
public class PersonServiceTest extends ServiceTest {
  
  private String docId;
  private String personId;
  private PersonTestData personTestData;
  private AttributeTestData attributeTestData;
  private EntityRelationTestData relationTestData;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    
    EntityManager em = getModel().getEntityManager();
    
    // set up test data
    personTestData = new PersonTestData();
    attributeTestData = new AttributeTestData();
    relationTestData = new EntityRelationTestData();
    Attribute testAttribute = attributeTestData.createTestAttribute();
    Person testPerson = personTestData.createTestPerson(1);
    Person relatedPerson = personTestData.createTestPerson(2);
    EntityRelation testRelation = relationTestData.createTestRelation(testPerson, relatedPerson);
    testPerson.getEntityRelations().add(testRelation);
    
    testPerson.getAttributes().add(testAttribute);
    Document testDoc = new DocumentTestData().createTestDocument(1);
    testDoc.getContent().getPersons().add(testPerson);
    
    // save ids for request
    docId = testDoc.getId();
    personId = testPerson.getId();
    
    // persist test data
    em.getTransaction().begin();
    em.persist(testAttribute);
    em.persist(testPerson);
    em.persist(testRelation);
    em.persist(relatedPerson);
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
    personTestData.checkData(responsePerson, 1);
    
    // check the attributes
    Set<Attribute> personsAttributes = responsePerson.getAttributes();
    
    assertNotNull(personsAttributes);
    assertEquals(1, personsAttributes.size());
    
    for (Attribute attribute: personsAttributes) {
      attributeTestData.checkData(attribute, 1);
    }
    
    Set<EntityRelation> relations = responsePerson.getEntityRelations();
    assertEquals(1, relations.size());
    
    // can only check whether the weight is correct, other fields are null
    assertEquals(EntityRelationTestData.TEST_ENTITY_RELATION_WEIGHT, relations.iterator().next().getWeight(), 0.001);
  }

}
