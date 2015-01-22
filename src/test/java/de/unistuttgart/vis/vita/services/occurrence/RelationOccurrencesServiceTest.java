package de.unistuttgart.vis.vita.services.occurrence;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Before;

import de.unistuttgart.vis.vita.data.ChapterTestData;
import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.data.EntityRelationTestData;
import de.unistuttgart.vis.vita.data.PersonTestData;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.document.Occurrence;
import de.unistuttgart.vis.vita.model.document.Sentence;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.model.entity.EntityRelation;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

/**
 * Performs test on RelationOccurrencesService, persisting persons with overlapping occurrences and
 * fetch their relation using REST.
 */
public class RelationOccurrencesServiceTest extends OccurrencesServiceTest {

  // TODO: Check test data and result, especially for sentences

  /*
   * SP for the separate Occurrences without an overlap
   */
  private static final int SP1_SENTENCE_START_OFFSET = 90000;
  private static final int SP1_START_OFFSET = 100000;
  private static final int SP1_END_OFFSET = 101000;
  private static final int SP1_SENTENCE_END_OFFSET = 102000;

  private static final int SP2_SENTENCE_START_OFFSET = 122000;
  private static final int SP2_START_OFFSET = 122000;
  private static final int SP2_END_OFFSET = 123000;
  private static final int SP2_SENTENCE_END_OFFSET = 124000;

  /*
   * OL for overlapping Occurrences with an overlap
   */
  private static final int OL_SENTENCE_START_OFFSET = 104000;
  private static final int OL1_START_OFFSET = 105000;
  private static final int OL1_END_OFFSET = 115000;

  private static final int OL2_START_OFFSET = 110000;
  private static final int OL2_END_OFFSET = 120000;
  private static final int OL_SENTENCE_END_OFFSET = 121000;

  /*
   * NEAR for Occurrences that are near to each other
   */
  private static final int NEAR1_START_OFFSET = 2000000;
  private static final int NEAR1_END_OFFSET = 2000100;

  private static final int NEAR1_SENTENCE_START_OFFSET = 1900000;
  private static final int NEAR1_SENTENCE_END_OFFSET = 2000110;

  private static final int NEAR2_START_OFFSET = 2000200;
  private static final int NEAR2_END_OFFSET = 2000300;

  private static final int NEAR2_SENTENCE_START_OFFSET = 2000190;
  private static final int NEAR2_SENTENCE_END_OFFSET = 2000900;

  private static final int OL_LENGTH = OL_SENTENCE_END_OFFSET - OL_SENTENCE_START_OFFSET;

  private PersonTestData personTestData;
  private EntityRelationTestData relationTestData;

  private String docId;
  private String entityIds;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();

    // set up test data
    this.personTestData = new PersonTestData();
    this.relationTestData = new EntityRelationTestData();

    // set up document
    Document testDoc = new DocumentTestData().createTestDocument(1);

    // set up test chapter
    Chapter testChapter = new ChapterTestData().createTestChapter();
    TextPosition chapterStart =
        TextPosition.fromGlobalOffset(0,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    TextPosition chapterEnd =
        TextPosition.fromGlobalOffset(DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Range chapterRange = new Range(chapterStart, chapterEnd);
    testChapter.setRange(chapterRange);

    // set up first person
    Person originPerson = personTestData.createTestPerson(1);

    // non overlapping TextSpan
    Range originSeparateSentenceRange =
        new Range(testChapter, SP1_SENTENCE_START_OFFSET, SP1_SENTENCE_END_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Sentence originSeparateSentence = new Sentence(originSeparateSentenceRange, testChapter, 0);
    TextPosition originSeparateStart =
        TextPosition.fromGlobalOffset(SP1_START_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    TextPosition originSeparateEnd =
        TextPosition.fromGlobalOffset(SP1_END_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Range originSeparateRange = new Range(originSeparateStart, originSeparateEnd);
    Occurrence originSeparateOccurrence =
        new Occurrence(originSeparateSentence, originSeparateRange);

    // overlapping TextSpan
    Range originSentenceRange =
        new Range(testChapter, OL_SENTENCE_START_OFFSET, OL_SENTENCE_END_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Sentence originSentence = new Sentence(originSentenceRange, testChapter, 4);
    TextPosition originStart =
        TextPosition.fromGlobalOffset(OL1_START_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    TextPosition originEnd =
        TextPosition.fromGlobalOffset(OL1_END_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Range originRange = new Range(originStart, originEnd);
    Occurrence originOccurrence = new Occurrence(originSentence, originRange);

    // TextSpans next to each other
    Range originNearSentenceRange =
        new Range(testChapter, NEAR1_SENTENCE_START_OFFSET, NEAR1_SENTENCE_END_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Sentence originNearSentence = new Sentence(originNearSentenceRange, testChapter, 6);
    TextPosition originNearStart =
        TextPosition.fromGlobalOffset(NEAR1_START_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    TextPosition originNearEnd =
        TextPosition.fromGlobalOffset(NEAR1_END_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Range originNearRange = new Range(originNearStart, originNearEnd);
    Occurrence originNearOccurrence = new Occurrence(originNearSentence, originNearRange);

    originPerson.getOccurrences().add(originOccurrence);
    originPerson.getOccurrences().add(originSeparateOccurrence);
    originPerson.getOccurrences().add(originNearOccurrence);

    // set up second person
    Person targetPerson = personTestData.createTestPerson(2);

    // non-overlapping TextSpan
    Range targetSeparateSentenceRange =
        new Range(testChapter, SP2_SENTENCE_START_OFFSET, SP2_SENTENCE_END_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Sentence targetSeparateSentence = new Sentence(targetSeparateSentenceRange, testChapter, 2);
    TextPosition targetSeparateStart =
        TextPosition.fromGlobalOffset(SP2_START_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    TextPosition targetSeparateEnd =
        TextPosition.fromGlobalOffset(SP2_END_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Range targetSeparateRange = new Range(targetSeparateStart, targetSeparateEnd);
    Occurrence targetSeparateOccurrence = new Occurrence(targetSeparateSentence, targetSeparateRange);

    // overlapping TextSpan
    TextPosition targetStart =
        TextPosition.fromGlobalOffset(OL2_START_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    TextPosition targetEnd =
        TextPosition.fromGlobalOffset(OL2_END_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Range targetRange = new Range(targetStart, targetEnd);
    Occurrence targetOccurrence = new Occurrence(originSentence, targetRange);

    // TextSpans next to each other
    Range targetNearSentenceRange =
        new Range(testChapter, NEAR2_SENTENCE_START_OFFSET, NEAR2_SENTENCE_END_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    TextPosition targetNearStart =
        TextPosition.fromGlobalOffset(NEAR2_START_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    TextPosition targetNearEnd =
        TextPosition.fromGlobalOffset(NEAR2_END_OFFSET,
            DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT);
    Range targetNearRange = new Range(targetNearStart, targetNearEnd);
    Occurrence targetNearOccurrence = new Occurrence(originNearSentence, targetNearRange);

    targetPerson.getOccurrences().add(targetOccurrence);
    targetPerson.getOccurrences().add(targetSeparateOccurrence);
    targetPerson.getOccurrences().add(targetNearOccurrence);

    // set up relation between them
    EntityRelation testRelation = relationTestData.createTestRelation(originPerson, targetPerson);

    // save ids for query
    docId = testDoc.getId();
    addId(originPerson.getId());
    addId(targetPerson.getId());

    DocumentPart testPart = new DocumentPart();
    testPart.getChapters().add(testChapter);
    testDoc.getContent().getParts().add(testPart);

    // persist test data
    EntityManager em = getModel().getEntityManager();
    em.getTransaction().begin();
    em.persist(chapterRange);
    em.persist(testChapter);
    em.persist(originOccurrence);
    em.persist(originSeparateOccurrence);
    em.persist(originNearOccurrence);
    em.persist(originPerson);
    em.persist(targetOccurrence);
    em.persist(targetSeparateOccurrence);
    em.persist(targetPerson);
    em.persist(targetNearOccurrence);
    em.persist(testRelation);
    em.persist(testPart);
    em.persist(testDoc);
    em.getTransaction().commit();
    em.close();
  }

  /**
   * Add given id to comma separated String with entity ids to search for.
   *
   * @param idToAdd - the id which should be added to the search string
   */
  private void addId(String idToAdd) {
    if (idToAdd == null || idToAdd.isEmpty()) {
      throw new IllegalArgumentException("Id must not be empty or null!");
    }

    if (entityIds == null) {
      entityIds = idToAdd;
    } else {
      entityIds = entityIds + "," + idToAdd;
    }
  }

  @Override
  protected Application configure() {
    return new ResourceConfig(RelationOccurrencesService.class);
  }

  @Override
  protected String getPath() {
    return "/documents/" + docId + "/entities/relations/occurrences";
  }

  @Override
  protected WebTarget prepareWebTarget(int steps, double rangeStart, double rangeEnd) {
    return super.prepareWebTarget(steps, rangeStart, rangeEnd).queryParam("entityIds", entityIds);
  }

  @Override
  public void testGetExactOccurrences() {
    OccurrencesResponse actualResponse =
        target(getPath()).queryParam("rangeStart", DEFAULT_RANGE_START)
            .queryParam("rangeEnd", DEFAULT_RANGE_END).queryParam("entityIds", entityIds).request()
            .get(OccurrencesResponse.class);
    assertNotNull(actualResponse);
    List<Range> receivedOccurrences = actualResponse.getOccurrences();

    assertThat(receivedOccurrences, hasSize(2));

    // check content of response
    Range receivedOccurrence = receivedOccurrences.get(0);
    assertEquals(OL_LENGTH, receivedOccurrence.getLength());
  }

  @Override
  public void testGetStepwiseOccurences() {
    OccurrencesResponse actualResponse =
        prepareWebTarget(DEFAULT_STEP_AMOUNT, DEFAULT_RANGE_START, DEFAULT_RANGE_END).request()
            .get(OccurrencesResponse.class);
    assertNotNull(actualResponse);
    List<Range> receivedOccurrences = actualResponse.getOccurrences();

    assertThat(receivedOccurrences, hasSize(2));

    // check content of response
    Range receivedOccurrence = receivedOccurrences.get(0);
    assertTrue(OL_LENGTH <= receivedOccurrence.getLength());
  }

}
