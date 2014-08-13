package de.unistuttgart.vis.vita.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Chapter;

/**
 * Performs tests whether instances of Chapter can be persisted correctly.
 */
public class ChapterPersistenceTest {

  // test data
  private static final int TEST_CHAPTER_LENGTH = 2531244;
  private static final int TEST_CHAPTER_NUMBER = 11;
  private static final String TEST_CHAPTER_TEXT = "This is a very short Chapter.";
  private static final String TEST_CHAPTER_TITLE = "A Knife in the Dark";

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
   * Checks whether one Chapter can be persisted.
   */
  @Test
  public void testPersistOneChapter() {
    // first set up a Chapter
    Chapter chapter = createTestChapter();

    // persist this chapter
    em.persist(chapter);
    em.getTransaction().commit();
    em.close();

    // read persisted chapters from database
    List<Chapter> chapters = readChaptersFromDb();

    // check whether data is correct
    assertEquals(1, chapters.size());
    Chapter readChapter = chapters.get(0);
    checkData(readChapter);
  }

  /**
   * Creates a new Chapter, sets attributes to test values and returns it.
   * 
   * @return test chapter
   */
  private Chapter createTestChapter() {
    Chapter chapter = new Chapter(null);

    chapter.setLength(TEST_CHAPTER_LENGTH);
    chapter.setNumber(TEST_CHAPTER_NUMBER);
    chapter.setText(TEST_CHAPTER_TEXT);
    chapter.setTitle(TEST_CHAPTER_TITLE);

    return chapter;
  }

  /**
   * Reads Chapters from database and returns them.
   * 
   * @return list of chapters
   */
  private List<Chapter> readChaptersFromDb() {
    em = model.getEntityManager();
    em.getTransaction().begin();

    TypedQuery<Chapter> query = em.createQuery("from Chapter", Chapter.class);
    List<Chapter> chapters = query.getResultList();

    em.getTransaction().commit();
    return chapters;
  }

  /**
   * Checks whether the given chapter is not <code>null</code> and includes the correct test data.
   * 
   * @param chapterToCheck - the chapter which should be checked
   */
  private void checkData(Chapter chapterToCheck) {
    assertNotNull(chapterToCheck);
    assertEquals(TEST_CHAPTER_LENGTH, chapterToCheck.getLength());
    assertEquals(TEST_CHAPTER_NUMBER, chapterToCheck.getNumber());
    assertEquals(TEST_CHAPTER_TEXT, chapterToCheck.getText());
    assertEquals(TEST_CHAPTER_TITLE, chapterToCheck.getTitle());
  }

  /**
   * Checks whether all Named Queries of Chapter are working correctly.
   */
  @Test
  public void testNamedQueries() {
    Chapter testChapter = createTestChapter();

    em.persist(testChapter);
    em.getTransaction().commit();
    em.close();

    em = model.getEntityManager();

    // check Named Query finding all chapters
    em.getTransaction().begin();
    TypedQuery<Chapter> allQ = em.createNamedQuery("Chapter.findAllChapters", Chapter.class);
    List<Chapter> allChapters = allQ.getResultList();
    em.getTransaction().commit();

    assertTrue(allChapters.size() > 0);
    Chapter readChapter = allChapters.get(0);
    checkData(readChapter);

    String id = readChapter.getId();

    // check Named Query finding chapters by id
    em.getTransaction().begin();
    TypedQuery<Chapter> idQ = em.createNamedQuery("Chapter.findChapterById", Chapter.class);
    idQ.setParameter("chapterId", id);
    Chapter idChapter = idQ.getSingleResult();
    em.getTransaction().commit();

    checkData(idChapter);

    // check Named Query finding chapters by title
    em.getTransaction().begin();
    TypedQuery<Chapter> titleQ = em.createNamedQuery("Chapter.findChapterByTitle", Chapter.class);
    titleQ.setParameter("chapterTitle", TEST_CHAPTER_TITLE);
    Chapter titleChapter = titleQ.getSingleResult();
    em.getTransaction().commit();

    checkData(titleChapter);
  }

  /**
   * Finally closes the EntityManager.
   */
  @After
  public void tearDown() {
    em.close();
  }

}
