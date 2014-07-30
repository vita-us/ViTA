package de.unistuttgart.vis.vita.test.model;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.Entity;
import de.unistuttgart.vis.vita.model.EntityRelation;
import de.unistuttgart.vis.vita.model.Person;

/**
 * Performs some simple tests on the class EntityRelation.
 * 
 * @author Marc Weise
 * @version 0.1 30.07.2014
 */
public class EntityRelationTest {

  // test data
  private static final double TEST_DELTA = 0.001;
  private static final double TEST_LEGAL_WEIGHT = 0.5;
  private static final double TEST_TOO_HIGH_WEIGHT = 2.0;
  private static final double TEST_TOO_LOW_WEIGHT = -1.0;

  // attributes
  private Person testPerson;
  private EntityRelation<Entity> relation;

  @Before
  public void setUp() throws Exception {
    testPerson = new Person();
    relation = new EntityRelation<Entity>();
    Set<EntityRelation<Entity>> relations = new HashSet<EntityRelation<Entity>>();
    relations.add(relation);
    testPerson.setEntityRelations(relations);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetTooLowWeight() {
    relation.setWeight(TEST_TOO_LOW_WEIGHT);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetTooHighWeight() {
    relation.setWeight(TEST_TOO_HIGH_WEIGHT);
  }

  @Test
  public void testSetAndGetWeight() {
    relation.setWeight(TEST_LEGAL_WEIGHT);
    assertEquals(TEST_LEGAL_WEIGHT, relation.getWeight(), TEST_DELTA);
  }

  @Test
  public void testSetAndGetRelatedEntity() {
    relation.setRelatedEntity(testPerson);
    assertEquals(testPerson, relation.getRelatedEntity());
  }

}
