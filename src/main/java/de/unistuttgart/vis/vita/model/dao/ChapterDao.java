package de.unistuttgart.vis.vita.model.dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.document.Chapter;

/**
 * Represents a data access object for accessing Chapters.
 */
@ManagedBean
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
    return getChaptersByOffsetQuery(docId, offset).getSingleResult();
  }
  
  /**
   * Returns the surrounding Chapters of a given offset.
   * 
   * @param docId - the id of the Document to search in
   * @param offset - the offset which lays in the chapters to find
   * @return list of surrounding chapters
   */
  public List<Chapter> findChaptersByOffset(String docId, int offset) {
    return getChaptersByOffsetQuery(docId, offset).getResultList();
  }
  
  private TypedQuery<Chapter> getChaptersByOffsetQuery(String docId, int offset) {
    TypedQuery<Chapter> query = em.createNamedQuery("Chapter.findChapterByOffset", Chapter.class);
    query.setParameter("documentId", docId);
    query.setParameter("offset", offset);
    return query;
  }

}
