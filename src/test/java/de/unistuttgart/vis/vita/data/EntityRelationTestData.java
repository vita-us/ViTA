package de.unistuttgart.vis.vita.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.EntityRelation;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.model.entity.Place;

/**
 * Holds test data for relations between entities and methods to create test relations and check 
 * whether given data matches the test data.
 */
public class EntityRelationTestData {
  
  public static final double TEST_ENTITY_RELATION_WEIGHT = 0.5;
  public static final double DELTA = 0.001;
  
  private PlaceTestData placeTestData;
  private PersonTestData personTestData;
  
  /**
   * Creates a new instance of EntityRelationTestData.
   */
  public EntityRelationTestData() {
    this.personTestData = new PersonTestData();
    this.placeTestData = new PlaceTestData();
  }

  /**
   * Creates a new test relation a given entity.
   * 
   * @param relatedEntity - the entity which should be related
   * @param relatedPerson 
   * @return test relation to given entity
   */
  public EntityRelation createTestRelation(Entity originEntity, Entity relatedEntity) {
    EntityRelation rel = new EntityRelation();
    rel.setOriginEntity(originEntity);
    rel.setRelatedEntity(relatedEntity);
    rel.setWeight(TEST_ENTITY_RELATION_WEIGHT);
    return rel;
  }

  /**
   * Checks whether the given entity relation is not <code>null</code> and includes the correct
   * test data.
   * 
   * @param entityRelationToCheck - the entity relation to be checked
   */
  public void checkData(EntityRelation entityRelationToCheck) {
    assertNotNull(entityRelationToCheck);
  
    Entity relatedEntity = entityRelationToCheck.getRelatedEntity();
    switch (relatedEntity.getType()) {
      case PERSON:
        personTestData.checkData((Person) relatedEntity, 2);
        break;
      case PLACE:
        placeTestData.checkData((Place) relatedEntity, 2);
        break;
      default:
        throw new IllegalArgumentException();
    }
  
    assertEquals(TEST_ENTITY_RELATION_WEIGHT, entityRelationToCheck.getWeight(), DELTA);
  }
  
}
