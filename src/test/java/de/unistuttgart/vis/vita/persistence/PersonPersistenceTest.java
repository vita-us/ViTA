package de.unistuttgart.vis.vita.persistence;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.TypedQuery;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.PersonTestData;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.Occurrence;
import de.unistuttgart.vis.vita.model.document.Sentence;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.model.entity.Person;

/**
 * Performs tests whether instances of Person can be persisted correctly.
 */
public class PersonPersistenceTest extends AbstractPersistenceTest {

  private PersonTestData testData;

  @Override
  public void setUp() {
    super.setUp();

    this.testData = new PersonTestData();
  }

  /**
   * Checks whether one Person can be persisted.
   */
  @Test
  public void testPersistOnePerson() {
    // first set up a Person
    Person p = testData.createTestPerson();

    // persist this person
    em.persist(p);
    startNewTransaction();

    // read persisted persons from database
    List<Person> persons = readPersonsFromDb();

    // check whether data is correct
    assertEquals(1, persons.size());
    Person readPerson = persons.get(0);

    testData.checkData(readPerson);
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
   * Checks whether all Named Queries of Person are working correctly.
   */
  @Test
  public void testNamedQueries() {
    Person testPerson = testData.createTestPerson(1);
    Person docTestPerson = testData.createTestPerson(2);

    Document testDoc = new DocumentTestData().createTestDocument(1);
    testDoc.getContent().getPersons().add(docTestPerson);

    String documentId = testDoc.getId();

    em.persist(testPerson);
    em.persist(docTestPerson);
    em.persist(testDoc);

    startNewTransaction();

    // check Named Query finding all persons
    TypedQuery<Person> allQ = em.createNamedQuery("Person.findAllPersons", Person.class);
    List<Person> allPersons = allQ.getResultList();

    assertTrue(0 < allPersons.size());
    Person readPerson = allPersons.get(0);
    testData.checkData(readPerson, 1);

    String id = readPerson.getId();

    // check Named Query finding all persons mentioned in a document
    TypedQuery<Person> query = em.createNamedQuery("Person.findPersonsInDocument", Person.class);
    query.setParameter("documentId", documentId);

    List<Person> docPersons = query.getResultList();

    assertEquals(1, docPersons.size());
    testData.checkData(docTestPerson, 2);

    // check Named Query finding person by id
    TypedQuery<Person> idQ = em.createNamedQuery("Person.findPersonById", Person.class);
    idQ.setParameter("personId", id);
    Person idPerson = idQ.getSingleResult();

    testData.checkData(idPerson, 1);

    // check Named Query finding persons by name
    TypedQuery<Person> nameQ = em.createNamedQuery("Person.findPersonByName", Person.class);
    nameQ.setParameter("personName", PersonTestData.TEST_PERSON_1_NAME);
    List<Person> namePersons = nameQ.getResultList();

    assertTrue(namePersons.size() > 0);
    Person namePerson = namePersons.get(0);
    testData.checkData(namePerson, 1);
  }

  /**
   * Checks whether Occurrences are sorted in the right order.
   */
  @Test
  public void testOcurrencesAreSorted() {
    Document doc = new Document();
    Chapter chapter = new Chapter();
    TextPosition pos1 =
        TextPosition.fromGlobalOffset(10, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    TextPosition pos2 =
        TextPosition.fromGlobalOffset(20, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    TextPosition pos3 =
        TextPosition.fromGlobalOffset(30, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    TextPosition pos4 =
        TextPosition.fromGlobalOffset(40, DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Range span1 = new Range(pos1, pos4);
    Range span2 = new Range(pos2, pos4);
    Range span3 = new Range(pos3, pos4);

    Range sentenceRange1 = new Range(pos1, pos4);
    Range sentenceRange2 = new Range(pos3, pos4);
    Sentence sentence1 = new Sentence(sentenceRange1, chapter, 0);
    Sentence sentence2 = new Sentence(sentenceRange2, chapter, 1);

    Range range1 = new Range(pos1, pos4);
    Range range2 = new Range(pos2, pos4);
    Range range3 = new Range(pos3, pos4);

    Occurrence occurrence1 = new Occurrence(sentence1, range1);
    Occurrence occurrence2 = new Occurrence(sentence1, range2);
    Occurrence occurrence3 = new Occurrence(sentence2, range3);

    Person p = new Person();
    // Add the occurrences in an order that is neither the correct one, nor the reverse
    p.getOccurrences().add(occurrence1);
    p.getOccurrences().add(occurrence2);
    p.getOccurrences().add(occurrence3);

    em.persist(doc);
    em.persist(chapter);
    em.persist(span1);
    em.persist(span3);
    em.persist(span2);
    em.persist(p);
    startNewTransaction();

    Person dbPerson = em.createNamedQuery("Person.findAllPersons", Person.class).getSingleResult();
    assertThat(dbPerson.getOccurrences(),
        IsIterableContainingInOrder.contains(occurrence1, occurrence2, occurrence3));
  }

}
