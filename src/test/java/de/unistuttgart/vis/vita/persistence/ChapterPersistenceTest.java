package de.unistuttgart.vis.vita.persistence;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.TypedQuery;

import org.junit.Test;

import de.unistuttgart.vis.vita.data.ChapterTestData;
import de.unistuttgart.vis.vita.model.document.Chapter;

/**
 * Performs tests whether instances of Chapter can be persisted correctly.
 */
public class ChapterPersistenceTest extends AbstractPersistenceTest {
  
  private ChapterTestData testData;
  
  /**
   * Creates a test data instance.
   */
  @Override
  public void setUp() {
    super.setUp();
    
    this.testData = new ChapterTestData();
  }

  /**
   * Checks whether one Chapter can be persisted.
   */
  @Test
  public void testPersistOneChapter() {
    // first set up a Chapter
    Chapter chapter = testData.createTestChapter();

    // persist this chapter
    em.persist(chapter);
    startNewTransaction();

    // read persisted chapters from database
    List<Chapter> chapters = readChaptersFromDb();

    // check whether data is correct
    assertEquals(1, chapters.size());
    Chapter readChapter = chapters.get(0);

    testData.checkData(readChapter);
  }

  /**
   * Reads Chapters from database and returns them.
   * 
   * @return list of chapters
   */
  private List<Chapter> readChaptersFromDb() {
    TypedQuery<Chapter> query = em.createQuery("from Chapter", Chapter.class);
    List<Chapter> chapters = query.getResultList();
    return chapters;
  }

  /**
   * Checks whether all Named Queries of Chapter are working correctly.
   */
  @Test
  public void testNamedQueries() {
    Chapter testChapter = testData.createTestChapter();

    em.persist(testChapter);
    startNewTransaction();

    // check Named Query finding all chapters
    TypedQuery<Chapter> allQ = em.createNamedQuery("Chapter.findAllChapters", Chapter.class);
    List<Chapter> allChapters = allQ.getResultList();

    assertTrue(allChapters.size() > 0);
    Chapter readChapter = allChapters.get(0);
    testData.checkData(readChapter);

    String id = readChapter.getId();

    // check Named Query finding chapters by id
    TypedQuery<Chapter> idQ = em.createNamedQuery("Chapter.findChapterById", Chapter.class);
    idQ.setParameter("chapterId", id);
    Chapter idChapter = idQ.getSingleResult();

    testData.checkData(idChapter);

    // check Named Query finding chapters by title
    TypedQuery<Chapter> titleQ = em.createNamedQuery("Chapter.findChapterByTitle", Chapter.class);
    titleQ.setParameter("chapterTitle", ChapterTestData.TEST_CHAPTER_TITLE);
    Chapter titleChapter = titleQ.getSingleResult();

    testData.checkData(titleChapter);
  }
}
