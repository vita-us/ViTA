package de.unistuttgart.vis.vita.services.occurrence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.data.AttributeTestData;
import de.unistuttgart.vis.vita.data.ChapterTestData;
import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.PersonTestData;
import de.unistuttgart.vis.vita.data.OccurrenceTestData;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.document.Occurrence;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

public class AttributeOccurrencesServiceTest extends OccurrencesServiceTest {

  private static final int ABSOLUTE_START_OFFSET = OccurrenceTestData.TEST_RANGE_START;
  private static final int ABSOLUTE_END_OFFSET = OccurrenceTestData.TEST_RANGE_END;

  private String documentId;
  private String entityId;
  private String attributeId;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();

    // first set up test data
    OccurrenceTestData testData = new OccurrenceTestData();
    Document testDoc = new DocumentTestData().createTestDocument(1);
    Chapter testChapter = new ChapterTestData().createTestChapter();

    // Set range
    TextPosition rangeStartPos =
        TextPosition.fromGlobalOffset(0,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    TextPosition rangeEndPos =
        TextPosition.fromGlobalOffset(DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);

    Range chapterRangeSpan = new Range(rangeStartPos, rangeEndPos);
    testChapter.setRange(chapterRangeSpan);

    Occurrence attributeOccurrence = testData.createOccurrence(testChapter);
    Person testEntity = new PersonTestData().createTestPerson(1);
    Attribute testAttribute = new AttributeTestData().createTestAttribute(1);
    testEntity.getAttributes().add(testAttribute);
    testAttribute.getOccurrences().add(attributeOccurrence);

    // id for query
    documentId = testDoc.getId();
    entityId = testEntity.getId();
    attributeId = testAttribute.getId();

    DocumentPart testPart = new DocumentPart();
    testPart.getChapters().add(testChapter);
    testDoc.getContent().getParts().add(testPart);

    // persist it
    EntityManager em = getModel().getEntityManager();
    em.getTransaction().begin();

    em.persist(chapterRangeSpan);
    em.persist(testChapter);
    em.persist(attributeOccurrence);
    em.persist(testEntity);
    em.persist(testAttribute);
    em.persist(testPart);
    em.persist(testDoc);
    em.getTransaction().commit();
    em.close();
  }

  @Override
  protected Application configure() {
    return new ResourceConfig(AttributeOccurrencesService.class);
  }

  /**
   * @return the path to the service under test
   */
  @Override
  protected String getPath() {
    return "/documents/" + documentId + "/entities/" + entityId + "/attributes/" + attributeId
        + "/occurrences";
  }

  /**
   * Checks whether the exact occurrences for an attribute of an entity can be caught via GET by not
   * passing a "step" parameter
   */
  @Test
  public void testGetExactOccurrences() {
    OccurrencesResponse actualResponse =
        target(getPath()).queryParam("rangeStart", DEFAULT_RANGE_START)
            .queryParam("rangeEnd", DEFAULT_RANGE_END).request().get(OccurrencesResponse.class);
    assertNotNull(actualResponse);
    checkOccurrences(actualResponse.getOccurrences(), true);
  }

  /**
   * Checks whether stepwise occurrences for attribute of an entity can be caught via GET.
   */
  @Test
  public void testGetStepwiseOccurences() {
    OccurrencesResponse actualResponse =
        target(getPath()).queryParam("steps", DEFAULT_STEP_AMOUNT)
            .queryParam("rangeStart", DEFAULT_RANGE_START)
            .queryParam("rangeEnd", DEFAULT_RANGE_END).request().get(OccurrencesResponse.class);

    // check response and amount of occurrences
    assertNotNull(actualResponse);
    checkOccurrences(actualResponse.getOccurrences(), false);
  }

  private void checkOccurrences(List<Range> occurrences, boolean exact) {
    assertEquals(1, occurrences.size());
    Range receivedOccurence = occurrences.get(0);

    TextPosition absoluteStart = receivedOccurence.getStart();
    TextPosition absoluteEnd = receivedOccurence.getEnd();

    // check values
    if (exact) {
      assertEquals(ABSOLUTE_START_OFFSET, absoluteStart.getOffset());
      assertEquals(ABSOLUTE_END_OFFSET, absoluteEnd.getOffset());
      assertEquals(OccurrenceTestData.TEST_RANGE_LENGTH, receivedOccurence.getLength());
    } else {
      assertTrue(ABSOLUTE_START_OFFSET >= absoluteStart.getOffset());
      assertTrue(ABSOLUTE_END_OFFSET <= absoluteEnd.getOffset());
      assertTrue(OccurrenceTestData.TEST_RANGE_LENGTH <= receivedOccurence.getLength());
    }
  }
}
