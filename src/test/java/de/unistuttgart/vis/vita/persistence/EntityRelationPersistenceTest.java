package de.unistuttgart.vis.vita.persistence;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import de.unistuttgart.vis.vita.data.EntityRelationTestData;
import de.unistuttgart.vis.vita.data.PersonTestData;
import de.unistuttgart.vis.vita.data.PlaceTestData;
import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.EntityRelation;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.model.entity.Place;

/**
 * Performs tests whether instances of EntityRelation can be persisted correctly.
 */
public class EntityRelationPersistenceTest extends AbstractPersistenceTest {
    
  private PersonTestData personTestData;
  private PlaceTestData placeTestData;
  private EntityRelationTestData relationTestData;
  
  @Override
  public void setUp() {
    super.setUp();
    
    // set up test data
    this.personTestData = new PersonTestData();
    this.placeTestData = new PlaceTestData();
    this.relationTestData = new EntityRelationTestData();
  }

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
   * Checks whether one EntityRelation can be persisted.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testPersistOnePersonRelation() {
    // first set up a entity relation
    Person testPerson = personTestData.createTestPerson(1);
    Person relatedPerson = personTestData.createTestPerson(2);
    EntityRelation<Entity> rel = relationTestData.createTestRelation(testPerson, relatedPerson);
    testPerson.getEntityRelations().add(rel);

    // persist this entity relation
    em.persist(testPerson);
    em.persist(relatedPerson);
    em.persist(rel);
    startNewTransaction();

    // read persisted entity relations
    List<EntityRelation<Person>> relations =
        (List<EntityRelation<Person>>) readEntityRelationsFromDb();

    // check whether data is correct
    assertEquals(1, relations.size());
    EntityRelation<Person> readRelation = relations.get(0);
    relationTestData.checkData(readRelation);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testPersistOnePlaceRelation() {
    // first set up a entity relation
    Place testPlace = placeTestData.createTestPlace(1);
    Place relatedPlace = placeTestData.createTestPlace(2);
    EntityRelation<Entity> rel = relationTestData.createTestRelation(testPlace, relatedPlace);
    testPlace.getEntityRelations().add(rel);

    // persist entities and their relation
    em.persist(testPlace);
    em.persist(relatedPlace);
    em.persist(rel);
    startNewTransaction();

    // read persisted entity relations
    List<EntityRelation<Place>> relations =
        (List<EntityRelation<Place>>) readEntityRelationsFromDb();

    // check whether data is correct
    assertEquals(1, relations.size());
    EntityRelation<Place> readRelation = relations.get(0);
    relationTestData.checkData(readRelation);
  }

  /**
   * Checks whether all Named Queries of EntityRelation are working correctly.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testNamedQueries() {
    Person testPerson = personTestData.createTestPerson(1);
    Person relatedPerson = personTestData.createTestPerson(2);
    EntityRelation<Entity> rel = relationTestData.createTestRelation(testPerson, relatedPerson);
    testPerson.getEntityRelations().add(rel);
    
    // persist entities and their relation
    em.persist(testPerson);
    em.persist(relatedPerson);
    em.persist(rel);
    startNewTransaction();
    
    // check Named Query finding all entity relations
    Query allQ = em.createNamedQuery("EntityRelation.findAllEntityRelations");
    List<EntityRelation<Person>> allEntityRelations = allQ.getResultList();
    
    assertTrue(allEntityRelations.size() > 0);
    EntityRelation<Person> readRelation = allEntityRelations.get(0);
    relationTestData.checkData(readRelation);
    
    String id = readRelation.getId();
    
    // check Named Query finding entity relation for entities
    List<String> entityIdList = new ArrayList<>();
    entityIdList.add(testPerson.getId());
    
    Query entQ = em.createNamedQuery("EntityRelation.findRelationsForEntities");
    entQ.setParameter("entityIds", entityIdList);
    List<EntityRelation<Person>> relations = entQ.getResultList();
    assertEquals(1, relations.size());
    relationTestData.checkData(relations.get(0));

    // check Named Query finding entity relation for entities
    Query entityTypeQ = em.createNamedQuery("EntityRelation.findRelationsForEntitiesAndType");
    entityTypeQ.setParameter("entityIds", entityIdList);
    entityTypeQ.setParameter("type", "Person");
    List<EntityRelation<Person>> entTypeRelations = entityTypeQ.getResultList();
    assertEquals(1, entTypeRelations.size());
    relationTestData.checkData(entTypeRelations.get(0));
    
    // check Named Query finding entity relation by id
    Query idQ = em.createNamedQuery("EntityRelation.findEntityRelationById");
    idQ.setParameter("entityRelationId", id);
    EntityRelation<Person> idRelation = (EntityRelation<Person>) idQ.getSingleResult();
    
    relationTestData.checkData(idRelation);
  }
  
}
