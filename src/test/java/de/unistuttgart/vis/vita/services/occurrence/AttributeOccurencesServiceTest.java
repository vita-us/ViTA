package de.unistuttgart.vis.vita.services.occurrence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import de.unistuttgart.vis.vita.data.TextSpanTestData;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.Person;
import de.unistuttgart.vis.vita.services.ServiceTest;
import de.unistuttgart.vis.vita.services.responses.occurrence.AbsoluteTextPosition;
import de.unistuttgart.vis.vita.services.responses.occurrence.Occurrence;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;

public class AttributeOccurencesServiceTest extends ServiceTest {
	private static final int ABSOLUTE_START_OFFSET = ChapterTestData.TEST_CHAPTER_RANGE_START
			+ TextSpanTestData.TEST_TEXT_SPAN_START;
	private static final double START_PROGRESS = ABSOLUTE_START_OFFSET
			/ (double) DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT;

	private static final int ABSOLUTE_END_OFFSET = ChapterTestData.TEST_CHAPTER_RANGE_START
			+ TextSpanTestData.TEST_TEXT_SPAN_END;
	private static final double END_PROGRESS = ABSOLUTE_END_OFFSET
			/ (double) DocumentTestData.TEST_DOCUMENT_CHARACTER_COUNT;

	private static final double DELTA = 0.001;

	private String documentId;
	private String entityId;
	private String chapterId;
	private String attributeId;

	@Before
	public void setUp() throws Exception {
		// first set up test data
		TextSpanTestData testData = new TextSpanTestData();
		Document testDoc = new DocumentTestData().createTestDocument(1);
		Chapter testChapter = new ChapterTestData().createTestChapter();

		// Set range
		TextPosition rangeStartPos = new TextPosition(null,
				ChapterTestData.TEST_CHAPTER_RANGE_START);
		TextPosition rangeEndPos = new TextPosition(null,
				ChapterTestData.TEST_CHAPTER_RANGE_END);

		TextSpan chapterRangeSpan = new TextSpan(rangeStartPos, rangeEndPos);
		testChapter.setRange(chapterRangeSpan);

		TextSpan attributeTextSpan = testData.createTestTextSpan(testChapter);
		Person testEntity = new PersonTestData().createTestPerson(1);
		Attribute testAttribute = new AttributeTestData()
				.createTestAttribute(1);
		testAttribute.getOccurrences().add(attributeTextSpan);

		// id for query
		documentId = testDoc.getId();
		chapterId = testChapter.getId();
		entityId = testEntity.getId();
		attributeId = testAttribute.getId();

		// persist it
		EntityManager em = Model.createUnitTestModel().getEntityManager();
		em.getTransaction().begin();
		em.persist(testDoc);
		em.persist(chapterRangeSpan);
		em.persist(testChapter);
		em.persist(attributeTextSpan);
		em.persist(testEntity);
		em.persist(testAttribute);
		em.getTransaction().commit();
		em.close();
	}

	protected Application configure() {
		return new ResourceConfig(AttributeOccurrencesService.class);
	}

	  /**
	   * Checks whether occurrences for attribute of an entity can be caught using the REST interface.
	   */
	@Test
	public void testGetOccurences() {
		
		String path = "/documents/" + documentId + "/entities/" + entityId
				+ "/attributes/" + attributeId + "/occurrences";
		
		OccurrencesResponse actualResponse = target(path)
				.queryParam("steps", 100).queryParam("rangeStart", 0.0)
				.queryParam("rangeEnd", 1.0).request()
				.get(OccurrencesResponse.class);
		
		// check response and amount of occurrences
		assertNotNull(actualResponse);
		checkOccurrences(actualResponse.getOccurrences());
	}

	private void checkOccurrences(List<Occurrence> occurrences) {
		assertEquals(1, occurrences.size());
	    
	    Occurrence receivedOccurence = occurrences.get(0);
	    
	    // check start position
	    AbsoluteTextPosition absoluteStart = receivedOccurence.getStart();
	    assertEquals(chapterId, absoluteStart.getChapter());
	    assertEquals(ABSOLUTE_START_OFFSET, absoluteStart.getOffset());
	    assertEquals(START_PROGRESS, absoluteStart.getProgress().doubleValue(), DELTA);
	    
	    // check end position
	    AbsoluteTextPosition absoluteEnd = receivedOccurence.getEnd();
	    assertEquals(chapterId, absoluteEnd.getChapter());
	    assertEquals(ABSOLUTE_END_OFFSET, absoluteEnd.getOffset());
	    assertEquals(END_PROGRESS, absoluteEnd.getProgress().doubleValue(), DELTA);
	    
	    // check the length
	    assertEquals(TextSpanTestData.TEST_TEXT_SPAN_LENGTH, receivedOccurence.getLength());
	}
}
