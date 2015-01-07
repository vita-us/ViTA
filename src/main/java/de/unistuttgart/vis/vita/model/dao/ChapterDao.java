package de.unistuttgart.vis.vita.model.dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.document.Chapter;

/**
 * Represents a data access object for accessing Chapters.
 */
@ManagedBean
@MappedSuperclass
@NamedQueries({
  @NamedQuery(name = "Chapter.findAllChapters",
      query = "SELECT c "
            + "FROM Chapter c"),

  @NamedQuery(name = "Chapter.findChapterById",
    query = "SELECT c "
          + "FROM Chapter c "
          + "WHERE c.id = :chapterId"),

  @NamedQuery(name = "Chapter.findChapterByTitle",
    query = "SELECT c "
          + "FROM Chapter c "
          + "WHERE c.title = :chapterTitle"),

  @NamedQuery(name = "Chapter.findChapterByOffset",
    query = "SELECT c "
          + "FROM Document d, DocumentPart dp, Chapter c "
          + "WHERE d.id = :documentId "
          + "AND dp MEMBER OF d.content.parts "
          + "AND c MEMBER OF dp.chapters "
          + "AND :offset BETWEEN c.range.start.offset AND c.range.end.offset")})
public class ChapterDao extends JpaDao<Chapter, String> {

  private static final String DOCUMENT_ID_PARAMETER = "documentId";
  private static final String OFFSET_PARAMETER = "offset";
  private static final String CHAPTER_TITLE_PARAMETER = "title";

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
    titleQuery.setParameter(CHAPTER_TITLE_PARAMETER, chapterTitle);
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
    query.setParameter(DOCUMENT_ID_PARAMETER, docId);
    query.setParameter(OFFSET_PARAMETER, offset);
    return query;
  }

}
