package de.unistuttgart.vis.vita.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.model.entity.Place;

/**
 * Performs tests whether instances of Entity can be persisted correctly.
 */
public class EntityPersistenceTest {

  private static final int TEST_PLACE_RANKING_VALUE = 3;
  private static final String TEST_PLACE_NAME = "Rivendell";
  // test data
  private static final int TEST_PERSON_RANKING_VALUE = 1;
  private static final String TEST_PERSON_NAME = "Frodo Baggins";

  // attributes
  private Model model;
  private EntityManager em;

  /**
   * Creates Model and EntityManager and starts the first Transaction.
   */
  @Before
  public void setUp() {
    model = new Model();
    em = model.getEntityManager();
    em.getTransaction().begin();
  }

  /**
   * Checks whether one Person can be persisted.
   */
  @Test
  public void testPersistOnePerson() {
    // first set up a Person
    Person p = new Person();
    p.setDisplayName(TEST_PERSON_NAME);
    p.setRankingValue(TEST_PERSON_RANKING_VALUE);

    // persist this person
    em.persist(p);
    em.getTransaction().commit();
    em.close();

    // read persisted persons from database
    List<Person> persons = readPersonsFromDb();
    
    // check whether data is correct
    assertEquals(1, persons.size());
    Person readPerson = persons.get(0);
    assertNotNull(readPerson);
    
    assertEquals(TEST_PERSON_NAME, readPerson.getDisplayName());
    assertEquals(TEST_PERSON_RANKING_VALUE, readPerson.getRankingValue());
  }

  /**
   * Reads Persons from database and returns them.
   * 
   * @return list of persons
   */
  private List<Person> readPersonsFromDb() {
    em = model.getEntityManager();
    em.getTransaction().begin();

    TypedQuery<Person> query = em.createQuery("from Person", Person.class);
    List<Person> result = query.getResultList();

    em.getTransaction().commit();
    return result;
  }
  
  /**
   * Checks whether one Place can be persisted correctly.
   */
  @Test
  public void testPersistOnePlace() {
    // first set up a place
    Place testPlace = new Place();
    testPlace.setDisplayName(TEST_PLACE_NAME);
    testPlace.setRankingValue(TEST_PLACE_RANKING_VALUE);
    
    // persist this place
    em.persist(testPlace);
    em.getTransaction().commit();
    em.close();
    
    // read places from the database
    List<Place> places = readPlacesFromDb();
    assertEquals(1, places.size());
    Place readPlace = places.get(0);
    assertNotNull(readPlace);
    assertEquals(TEST_PLACE_NAME, readPlace.getDisplayName());
    assertEquals(TEST_PLACE_RANKING_VALUE, readPlace.getRankingValue());
  }
  
  /**
   * Reads Places from database and returns them.
   * 
   * @return list of places
   */
  private List<Place> readPlacesFromDb() {
    em = model.getEntityManager();
    em.getTransaction().begin();
    
    TypedQuery<Place> query = em.createQuery("from Place", Place.class);
    List<Place> result = query.getResultList();
    
    return result;
  }

  /**
   * Finally closes the EntityManager.
   */
  public void tearDown() {
    em.close();
  }
  
}
