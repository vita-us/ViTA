package de.unistuttgart.vis.vita.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.TypedQuery;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.model.entity.Person;

/**
 * Performs tests whether instances of Person can be persisted correctly.
 */
public class PersonPersistenceTest extends AbstractPersistenceTest {
  
  // test data
  private static final int TEST_PERSON_RANKING_VALUE = 1;
  private static final String TEST_PERSON_NAME = "Frodo Baggins";

  /**
   * Checks whether one Person can be persisted.
   */
  @Test
  public void testPersistOnePerson() {
    // first set up a Person
    Person p = createTestPerson();

    // persist this person
    em.persist(p);
    startNewTransaction();

    // read persisted persons from database
    List<Person> persons = readPersonsFromDb();

    // check whether data is correct
    assertEquals(1, persons.size());
    Person readPerson = persons.get(0);
    
    checkData(readPerson);
  }

  /**
   * Creates a new Person, setting attributes to test values and returns it.
   * 
   * @return test person
   */
  private Person createTestPerson() {
    Person p = new Person();
    
    p.setDisplayName(TEST_PERSON_NAME);
    p.setRankingValue(TEST_PERSON_RANKING_VALUE);
    
    return p;
  }
  
  /**
   * Reads Persons from database and returns them.
   * 
   * @return list of persons
   */
  private List<Person> readPersonsFromDb() {
    TypedQuery<Person> query = em.createQuery("from Person", Person.class);
    return query.getResultList();
  }
  
  /**
   * Checks whether the given person is not <code>null</code> and includes the correct test data.
   * 
   * @param personToCheck - the person to be checked
   */
  private void checkData(Person personToCheck) {
    assertNotNull(personToCheck);
    assertEquals(TEST_PERSON_NAME, personToCheck.getDisplayName());
    assertEquals(TEST_PERSON_RANKING_VALUE, personToCheck.getRankingValue());
  }
  
  /**
   * Checks whether all Named Queries of Person are working correctly.
   */
  @Test
  public void testNamedQueries() {
    Person testPerson = createTestPerson();
    
    em.persist(testPerson);
    startNewTransaction();
    
    // check Named Query finding all persons
    TypedQuery<Person> allQ = em.createNamedQuery("Person.findAllPersons", Person.class);
    List<Person> allPersons = allQ.getResultList();
    
    assertTrue(allPersons.size() > 0);
    Person readPerson = allPersons.get(0);
    checkData(readPerson);
    
    int id = readPerson.getId();
    
    // check Named Query finding person by id
    TypedQuery<Person> idQ = em.createNamedQuery("Person.findPersonById", Person.class);
    idQ.setParameter("personId", id);
    Person idPerson = idQ.getSingleResult();
    
    checkData(idPerson);
    
    // check Named Query finding persons by name
    TypedQuery<Person> nameQ = em.createNamedQuery("Person.findPersonByName", Person.class);
    nameQ.setParameter("personName", TEST_PERSON_NAME);
    List<Person> namePersons = nameQ.getResultList();
    
    assertTrue(namePersons.size() > 0);
    Person namePerson = namePersons.get(0);
    checkData(namePerson);
  }
  
  @Test
  public void testOcurrencesAreSorted() {
    Document doc = new Document();
    Chapter chapter = new Chapter(doc);
    TextPosition pos1 = new TextPosition(chapter, 10);
    TextPosition pos2 = new TextPosition(chapter, 20);
    TextPosition pos3 = new TextPosition(chapter, 30);
    TextPosition pos4 = new TextPosition(chapter, 40);
    TextSpan span1 = new TextSpan(pos1, pos4);
    TextSpan span2 = new TextSpan(pos2, pos4);
    TextSpan span3 = new TextSpan(pos3, pos4);
    
    Person p = new Person();
    // Add the occurrences in an order that is neither the correct one, nor the reverse
    p.getOccurrences().add(span1);
    p.getOccurrences().add(span3);
    p.getOccurrences().add(span2);

    em.persist(doc);
    em.persist(chapter);
    em.persist(span1);
    em.persist(span3);
    em.persist(span2);
    em.persist(p);
    startNewTransaction();
    
    Person dbPerson = em.createNamedQuery("Person.findAllPersons", Person.class).getSingleResult();
    assertThat(dbPerson.getOccurrences(),
        IsIterableContainingInOrder.contains(span1, span2, span3));
  }

}
