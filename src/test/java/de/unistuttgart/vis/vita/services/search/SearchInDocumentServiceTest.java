package de.unistuttgart.vis.vita.services.search;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Before;

import com.google.common.collect.ImmutableList;

import de.unistuttgart.vis.vita.data.ChapterTestData;
import de.unistuttgart.vis.vita.data.DocumentPartTestData;
import de.unistuttgart.vis.vita.data.DocumentTestData;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.services.document.DocumentService;
import de.unistuttgart.vis.vita.services.occurrence.OccurrencesServiceTest;
import de.unistuttgart.vis.vita.services.responses.occurrence.OccurrencesResponse;


public class SearchInDocumentServiceTest extends OccurrencesServiceTest {
  private String documentId;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();

    // first set up test data
    Document testDoc = new DocumentTestData().createTestDocument(1);
    DocumentPart testPart = new DocumentPartTestData().createTestDocumentPart();
    testDoc.getContent().getParts().add(testPart);
    Chapter testChapter = new ChapterTestData().createTestChapter();
    testChapter.setLength(testChapter.getText().length());
    testChapter.setRange(new Range(
        TextPosition.fromGlobalOffset(0, testChapter.getLength()),
        TextPosition.fromGlobalOffset(testChapter.getLength(), testChapter.getLength())));
    testDoc.getMetrics().setCharacterCount(testChapter.getLength());
    testPart.getChapters().add(testChapter);

    // id for query
    documentId = testDoc.getId();

    // persist it
    EntityManager em = getModel().getEntityManager();
    em.getTransaction().begin();
    em.persist(testDoc);
    em.persist(testChapter);
    em.persist(testPart);
    em.getTransaction().commit();
    em.close();

    getModel().getTextRepository().storeChaptersTexts(ImmutableList.of(testChapter), documentId);
  }

  @Override
  protected Application configure() {
    return new ResourceConfig(SearchInDocumentService.class, DocumentService.class);
  }

  /**
   * @return the path to the service under test
   */
  @Override
  protected String getPath() {
    return "/documents/" + documentId + "/search";
  }

  @Override
  public void testGetExactOccurrences() {
    OccurrencesResponse actualResponse = target(getPath())
        .queryParam("rangeStart", DEFAULT_RANGE_START)
        .queryParam("rangeEnd", DEFAULT_RANGE_END)
        .queryParam("query", "very")
        .request().get(OccurrencesResponse.class);
    assertNotNull(actualResponse);
    assertThat(actualResponse.getOccurrences(), hasSize(1));
    Range occurrence = actualResponse.getOccurrences().get(0);
    assertThat(occurrence.getStart().getOffset(), is(10));
    assertThat(occurrence.getEnd().getOffset(), is(13));
  }

  @Override
  public void testGetStepwiseOccurences() {
    OccurrencesResponse actualResponse = target(getPath())
        .queryParam("rangeStart", DEFAULT_RANGE_START)
        .queryParam("rangeEnd", DEFAULT_RANGE_END)
        .queryParam("steps", 2)
        .queryParam("query", "very")
        .request().get(OccurrencesResponse.class);
    assertNotNull(actualResponse);
    assertThat(actualResponse.getOccurrences(), hasSize(1));
    Range occurrence = actualResponse.getOccurrences().get(0);
    assertThat(occurrence.getStart().getOffset(), is(0));
    assertThat(occurrence.getEnd().getOffset(), is(14));
  }

}
