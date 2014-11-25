package de.unistuttgart.vis.vita.services.entity;

import de.unistuttgart.vis.vita.data.*;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.entity.*;
import de.unistuttgart.vis.vita.services.ServiceTest;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public abstract class EntityServiceTest extends ServiceTest {

  protected EntityManager em;

  protected String docId;
  protected String entityId;

  protected PersonTestData personTestData;
  protected PlaceTestData placeTestData;
  protected AttributeTestData attributeTestData;
  protected EntityRelationTestData relationTestData;

  protected Document testDoc;

  @Override
  public void setUp() throws Exception {
    super.setUp();

    em = getModel().getEntityManager();

    // set up test data
    personTestData = new PersonTestData();
    placeTestData = new PlaceTestData();
    attributeTestData = new AttributeTestData();
    relationTestData = new EntityRelationTestData();

    // create document
    testDoc = new DocumentTestData().createTestDocument(1);
    docId = testDoc.getId();
  }

  /**
   * Checks whether an Entity can be caught using REST.
   */
  @Test
  public abstract void testGetEntity();

  /**
   * Checks a given Entities values, including attributes and relations matching the test data.
   *
   * @param entityToCheck - the Entity to be checked
   */
  protected void check(Entity entityToCheck) {
    checkEntity(entityToCheck);
    checkAttributes(entityToCheck.getAttributes());
    checkRelations(entityToCheck.getEntityRelations());
  }

  /**
   * Checks whether a given Entity matches the test Data.
   *
   * @param entityToCheck - the entity to be checked
   */
  private void checkEntity(Entity entityToCheck) {
    switch (entityToCheck.getType()) {
      case PERSON:
        personTestData.checkData((Person) entityToCheck);
        break;

      case PLACE:
        placeTestData.checkData((Place) entityToCheck);
        break;
    }
  }

  /**
   * Checks whether the given Set of Attributes matches the test data.
   *
   * @param attributesToCheck - the Set of Attribute to be checked
   */
  private void checkAttributes(Set<Attribute> attributesToCheck) {
    assertNotNull(attributesToCheck);
    assertEquals(1, attributesToCheck.size());

    for (Attribute attribute: attributesToCheck) {
      attributeTestData.checkData(attribute, 1);
    }
  }

  /**
   * Checks whether the given Set of EntityRelations matches the test data.
   *
   * @param relationsToCheck - the Set of EntityRelations to be checked
   */
  private void checkRelations(Set<EntityRelation> relationsToCheck) {
    assertEquals(1, relationsToCheck.size());

    // can only check whether the weight is correct, other fields are null
    assertEquals(EntityRelationTestData.TEST_ENTITY_RELATION_WEIGHT, relationsToCheck.iterator().next().getWeight(), 0.001);
  }

  /**
   * Returns the path to a service returning the given result.
   *
   * @param resultName - the name of the result which the service returns.
   * @return the service path
   */
  protected String getPath(String resultName) {
    return "documents/" + docId + "/" + resultName + "/" + entityId;
  }

}