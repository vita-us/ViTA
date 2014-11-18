package de.unistuttgart.vis.vita.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.entity.Entity;
import de.unistuttgart.vis.vita.model.entity.EntityRelation;
import de.unistuttgart.vis.vita.model.entity.Person;

/**
 * Performs some simple tests on the class EntityRelation.
 */
public class EntityRelationTest {

  // test data
  private static final double TEST_DELTA = 0.001;
  private static final double TEST_LEGAL_WEIGHT = 0.5;
  private static final double TEST_TOO_HIGH_WEIGHT = 2.0;
  private static final double TEST_TOO_LOW_WEIGHT = -1.0;

  // attributes
  private Person testPerson;
  private EntityRelation relation;

  /**
   * Sets up the testPerson and its relations.
   */
  @Before
  public void setUp() {
    testPerson = new Person();
    relation = new EntityRelation();
    testPerson.getEntityRelations().add(relation);
  }

  /**
   * Checks whether setting weight to a too low value causes an IllegalArgumentException to be
   * thrown.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testSetTooLowWeight() {
    relation.setWeight(TEST_TOO_LOW_WEIGHT);
  }

  /**
   * Checks whether setting weight to a too high value causes an IllegalArgumentException to be
   * thrown.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testSetTooHighWeight() {
    relation.setWeight(TEST_TOO_HIGH_WEIGHT);
  }

  /**
   * Checks whether Setter and Getter for the weight of an EntityRelation are working.
   */
  @Test
  public void testSetAndGetWeight() {
    relation.setWeight(TEST_LEGAL_WEIGHT);
    assertEquals(TEST_LEGAL_WEIGHT, relation.getWeight(), TEST_DELTA);
  }

  /**
   * Checks whether Setter and Getter for the related entity of an EntityRelation are working.
   */
  @Test
  public void testSetAndGetRelatedEntity() {
    relation.setRelatedEntity(testPerson);
    assertEquals(testPerson, relation.getRelatedEntity());
  }

}
