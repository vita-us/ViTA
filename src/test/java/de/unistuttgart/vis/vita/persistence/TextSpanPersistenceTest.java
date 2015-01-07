package de.unistuttgart.vis.vita.persistence;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.TypedQuery;

import org.junit.Test;

import de.unistuttgart.vis.vita.data.ChapterTestData;
import de.unistuttgart.vis.vita.data.PersonTestData;
import de.unistuttgart.vis.vita.data.TextSpanTestData;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.model.entity.Person;

/**
 * Performs tests whether instances of TextSpan can be persisted correctly.
 */
public class TextSpanPersistenceTest extends AbstractPersistenceTest {

  private static final int TEST_TEXT_SPAN_START = 10000;
  private static final int TEST_TEXT_SPAN_END = 11000;
  private static final int TEST_TEXT_SPAN_DIFF = 1000;

  private Chapter chapter = new Chapter();
  
  @Test
  public void testPersistOneTextSpan() {
    // first set up a TextSpan
    em.persist(chapter);
    Range ts = createTestTextSpan();
    
    // persist this TextSpan
    em.persist(ts);
    startNewTransaction();
    
    // read persisted TextSpans from database
    Range readTextSpan = readTextSpanFromDb(ts.getId());
    
    // check whether data is correct
    checkData(readTextSpan);
  }

  /**
   * Creates a new TextSpan, setting start and end to test values.
   * 
   * @return test text span
   */
  private Range createTestTextSpan() {
    return new Range(chapter, TEST_TEXT_SPAN_START, TEST_TEXT_SPAN_END);
  }
  
  /**
   * Read a specific TextSpans from database and returns it.
   * 
   * @return the text span
   */
  private Range readTextSpanFromDb(String id) {
    return em.createNamedQuery("TextSpan.findTextSpanById", Range.class)
        .setParameter("textSpanId", id)
        .getSingleResult();
  }
  
  /**
   * Checks whether the given TextSpan is not <code>null</code> and includes the correct test data.
   * 
   * @param textSpanToCheck - the TextSpan which should be checked
   */
  private void checkData(Range textSpanToCheck) {
    assertNotNull(textSpanToCheck);
    assertNotNull(textSpanToCheck.getId());
    
    int start = textSpanToCheck.getStart().getOffset();
    int end = textSpanToCheck.getEnd().getOffset();
    int diff = end - start;
    
    assertEquals(TEST_TEXT_SPAN_START, textSpanToCheck.getStart().getOffset());
    assertEquals(TEST_TEXT_SPAN_END, textSpanToCheck.getEnd().getOffset());
    assertEquals(TEST_TEXT_SPAN_DIFF, diff);
  }
  
  /**
   * Checks whether all Named Queries of TextSpan are working correctly.
   * @throws Exception
   */
  @Test
  public void testFindingAllAndSpecificTextSpans() {
    Range testTextSpan = createTestTextSpan();
    
    em.persist(chapter);
    em.persist(testTextSpan);
    startNewTransaction();
    
    // check Named Query finding all chapters
    TypedQuery<Range> allQ = em.createNamedQuery("TextSpan.findAllTextSpans", Range.class);
    List<Range> allSpans = allQ.getResultList();
    
    assertTrue(allSpans.size() > 0);
    Range readSpan = allSpans.get(0);
    checkData(readSpan);
    
    String id = readSpan.getId();
    
    // check Named Query finding text spans by id
    TypedQuery<Range> idQ = em.createNamedQuery("TextSpan.findTextSpanById", Range.class);
    idQ.setParameter("textSpanId", id);
    Range idTextSpan = idQ.getSingleResult();
    
    checkData(idTextSpan);
  }

  /**
   * Checks whether TextSpans for a specific entity can be found using a named query.
   */
  @Test
  public void testFindingSpanForEntity() {
    // first set up test data
    TextSpanTestData testData = new TextSpanTestData();
    Chapter c = new ChapterTestData().createTestChapter();
    Range personTextSpan = testData.createTestTextSpan(c);
    Person testPerson = new PersonTestData().createTestPerson(1);
    testPerson.getOccurrences().add(personTextSpan);
    
    // save ids for query
    String chapterId = c.getId();
    String personId = testPerson.getId();
    
    // now persist it
    em.persist(c);
    em.persist(personTextSpan);
    em.persist(testPerson);
    startNewTransaction();
    
    // read TextSpans from database
    TypedQuery<Range> personQ = em.createNamedQuery("TextSpan.findTextSpansForEntity", 
                                                        Range.class);
    personQ.setParameter("entityId", personId);
    personQ.setParameter("rangeStart", 0);
    personQ.setParameter("rangeEnd", 200000);
    
    List<Range> actualPersonTextSpans = personQ.getResultList();
    
    // finally check read data
    assertEquals(1, actualPersonTextSpans.size());
    testData.checkData(actualPersonTextSpans.get(0), chapterId);
  }

}
