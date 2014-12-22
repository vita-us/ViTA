package de.unistuttgart.vis.vita.model.dao;

import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.document.Chapter;

/**
 * Represents a data access object for accessing Chapters.
 */
public class ChapterDao extends JpaDao<Chapter, String> {

  /**
   * Creates a new data access object to access Chapters.
   */
  public ChapterDao() {
    super(Chapter.class);
  }
  
  /**
   * Find the Chapter with the given name.
   * 
   * @param chapterTitle - the title of the Chapter to search for
   * @return the Chapter matching the given title
   */
  public Chapter findChapterByTitle(String chapterTitle) {
    TypedQuery<Chapter> titleQuery = em.createNamedQuery("Chapter.findChapterByTitle", 
                                                          Chapter.class);
    titleQuery.setParameter("title", chapterTitle);
    return titleQuery.getSingleResult();
  }
  
  /**
   * Finds the surrounding Chapter of a given offset.
   * 
   * @param docId - the id of the Document to search in
   * @param offset - the offset which lays in the chapter to find
   * @return the surrounding Chapter
   */
  public Chapter findChapterByOffset(String docId, int offset) {
    TypedQuery<Chapter> offsetQuery = em.createNamedQuery("Chapter.findChapterByOffset", 
                                                          Chapter.class);
    offsetQuery.setParameter("documentId", docId);
    offsetQuery.setParameter("offset", offset);
    
    return offsetQuery.getSingleResult();
  }

}
