package de.unistuttgart.vis.vita.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.EntityRelation;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.model.entity.Place;

/**
 * Performs tests whether instances of EntityRelation can be persisted correctly.
 */
public class EntityRelationPesistenceTest extends AbstractPersistenceTest {
  
  // test data
  private static final double TEST_ENTITY_RELATION_WEIGHT = 0.5;
  private static final String TEST_PERSON_NAME = "Bilbo Baggins";
  private static final String TEST_PLACE_NAME = "Bag End";
  
  // constants
  private static final double DELTA = 0.001;

  /**
   * Reads EntityRelations from database and returns them.
   * 
   * @return list of entity relations
   */
  private List<?> readEntityRelationsFromDb() {
    Query query = em.createQuery("from EntityRelation", EntityRelation.class);
    return query.getResultList();
  }

  /**
   * Checks whether the given entity relation is not <code>null</code> and includes the correct
   * test data.
   * 
   * @param entityRelationToCheck - the entity relation to be checked
   */
  private void checkData(EntityRelation<? extends Entity> entityRelationToCheck) {
    assertNotNull(entityRelationToCheck);
  
    Entity relatedEntity = entityRelationToCheck.getRelatedEntity();
    switch (relatedEntity.getType()) {
      case PERSON:
        assertEquals(TEST_PERSON_NAME, relatedEntity.getDisplayName());
        break;
      case PLACE:
        assertEquals(TEST_PLACE_NAME, relatedEntity.getDisplayName());
        break;
      default:
        throw new IllegalArgumentException();
    }
  
    assertEquals(TEST_ENTITY_RELATION_WEIGHT, entityRelationToCheck.getWeight(), DELTA);
  }

  /**
   * Checks whether one EntityRelation can be persisted.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testPersistOnePersonRelation() {
    // first set up a entity relation
    Person testPerson = createTestPerson();
    EntityRelation<Person> rel = createTestPersonRelation(testPerson);
    
    // persist this entity relation
    em.persist(testPerson);
    em.persist(rel);
    startNewTransaction();
    
    // read persisted entity relations
    List<EntityRelation<Person>> relations = (List<EntityRelation<Person>>) readEntityRelationsFromDb();
    
    // check whether data is correct
    assertEquals(1, relations.size());
    EntityRelation<Person> readRelation = relations.get(0);
    checkData(readRelation);
  }
  
  /**
   * Creates a new Person, setting attributes to test values and returns it.
   * 
   * @return test person
   */
  private Person createTestPerson() {
    Person relatedPerson = new Person();
    relatedPerson.setDisplayName(TEST_PERSON_NAME);
    
    return relatedPerson;
  }
  
  /**
   * Creates a new relation to a person, setting attributes to test values and returns it.
   * 
   * @return test entity relation
   */
  private EntityRelation<Person> createTestPersonRelation(Person relatedPerson) {
    EntityRelation<Person> relation = new EntityRelation<>();
    relation.setWeight(TEST_ENTITY_RELATION_WEIGHT);
    relation.setRelatedEntity(relatedPerson);
    return relation;
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testPersistOnePlaceRelation() {
    // first set up a entity relation
    Place testPlace = createTestPlace();
    EntityRelation<Place> rel = createTestPlaceRelation(testPlace);
    
    // persist this entity relation
    em.persist(testPlace);
    em.persist(rel);
    startNewTransaction();
    
    // read persisted entity relations
    List<EntityRelation<Place>> relations = (List<EntityRelation<Place>>) readEntityRelationsFromDb();
    
    // check whether data is correct
    assertEquals(1, relations.size());
    EntityRelation<Place> readRelation = relations.get(0);
    checkData(readRelation);
  }

  /**
   * Creates a new Place, setting attributes to test values and returns it.
   * 
   * @return test place
   */
  private Place createTestPlace() {
    Place relatedPlace = new Place();
    relatedPlace.setDisplayName(TEST_PLACE_NAME);
    
    return relatedPlace;
  }

  /**
   * Creates a new relation to a place, setting attributes to test values and returns it.
   * 
   * @return test entity relation
   */
  private EntityRelation<Place> createTestPlaceRelation(Place relatedPlace) {
    EntityRelation<Place> relation = new EntityRelation<>();
    relation.setRelatedEntity(relatedPlace);
    relation.setWeight(TEST_ENTITY_RELATION_WEIGHT);
    return relation;
  }
  
  /**
   * Checks whether all Named Queries of EntityRelation are working correctly.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testNamedQueries() {
    Person testPerson = createTestPerson();
    
    EntityRelation<Person> testPersonRelation = createTestPersonRelation(testPerson);
    
    em.persist(testPerson);
    em.persist(testPersonRelation);
    startNewTransaction();
    
    // check Named Query finding all entity relations
    Query allQ = em.createNamedQuery("EntityRelation.findAllEntityRelations");
    List<EntityRelation<Person>> allEntityRelations = allQ.getResultList();
    
    assertTrue(allEntityRelations.size() > 0);
    EntityRelation<Person> readRelation = allEntityRelations.get(0);
    checkData(readRelation);
    
    int id = readRelation.getId();
    
    // check Named Query finding entity relation by id
    Query idQ = em.createNamedQuery("EntityRelation.findEntityRelationById");
    idQ.setParameter("entityRelationId", id);
    EntityRelation<Person> idRelation = (EntityRelation<Person>) idQ.getSingleResult();
    
    checkData(idRelation);
  }
  
}
