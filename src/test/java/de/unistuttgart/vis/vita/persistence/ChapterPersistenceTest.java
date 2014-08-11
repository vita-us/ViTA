package de.unistuttgart.vis.vita.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    Chapter chapter = new Chapter(null);
    chapter.setLength(TEST_CHAPTER_LENGTH);
    chapter.setNumber(TEST_CHAPTER_NUMBER);
    chapter.setText(TEST_CHAPTER_TEXT);
    chapter.setTitle(TEST_CHAPTER_TITLE);
    
    // persist this chapter
    em.persist(chapter);
    em.getTransaction().commit();
    em.close();
    
    // read persisted chapters from database
    List<Chapter> chapters = readChaptersFromDb();
    
    // check whether data is correct
    assertEquals(1, chapters.size());
    Chapter readChapter = chapters.get(0);
    assertNotNull(readChapter);
    assertEquals(TEST_CHAPTER_LENGTH, readChapter.getLength());
    assertEquals(TEST_CHAPTER_NUMBER, readChapter.getNumber());
    assertEquals(TEST_CHAPTER_TEXT, readChapter.getText());
    assertEquals(TEST_CHAPTER_TITLE, readChapter.getTitle());
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
   * Finally closes the EntityManager.
   */
  @After
  public void tearDown() {
    em.close();
  }
  
}
