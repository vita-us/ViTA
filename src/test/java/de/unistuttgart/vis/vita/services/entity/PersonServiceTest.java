package de.unistuttgart.vis.vita.services.entity;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.EntityRelation;
import de.unistuttgart.vis.vita.model.entity.Person;

/**
 * Performs tests on PersonService to check whether GET works correctly.
 */
public class PersonServiceTest extends EntityServiceTest {
  
  @Override
  protected Application configure() {
    return new ResourceConfig(PersonService.class);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();

    // create persons
    Person testPerson = personTestData.createTestPerson(1);
    Person relatedPerson = personTestData.createTestPerson(2);

    // create relation
    EntityRelation testRelation = relationTestData.createTestRelation(testPerson, relatedPerson);
    testPerson.getEntityRelations().add(testRelation);

    // create attribute
    Attribute testAttribute = attributeTestData.createTestAttribute();
    testPerson.getAttributes().add(testAttribute);

    // add persons to document
    testDoc.getContent().getPersons().add(testPerson);

    // save id for request
    entityId = testPerson.getId();

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

  /**
   * Checks whether a Person can be got using REST.
   */
  @Test
  public void testGetPerson() {
    Person responsePerson = target(getPath("persons")).request().get(Person.class);
    check(responsePerson);
  }

  @Override
  public void testGetEntity() {
    Person responseEntity = target(getPath("entities")).request().get(Person.class);
    check(responseEntity);
  }
}
