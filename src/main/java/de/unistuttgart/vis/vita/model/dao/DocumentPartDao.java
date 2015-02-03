package de.unistuttgart.vis.vita.model.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.document.DocumentPart;

/**
 * Represents a data access object for accessing DocumentParts.
 */
@MappedSuperclass
@NamedQueries({
  @NamedQuery(name = "DocumentPart.findAllParts",
              query = "SELECT dp "
                      + "FROM DocumentPart dp"),

  @NamedQuery(name = "DocumentPart.findPartsInDocument",
              query = "SELECT dp "
                      + "FROM DocumentPart dp, Document d "
                      + "WHERE d.id = :documentId "
                      + "AND dp MEMBER OF d.content.parts "
                      + "ORDER BY dp.number"),

  @NamedQuery(name = "DocumentPart.findPartById",
              query = "SELECT dp "
                      + "FROM DocumentPart dp "
                      + "WHERE dp.id = :partId"),

  @NamedQuery(name = "DocumentPart.findPartByTitle",
              query = "SELECT dp "
                      + "FROM DocumentPart dp "
                      + "WHERE dp.title = :partTitle"),

  @NamedQuery(name = "DocumentPart.getNumberOfPartsInDocument",
              query = "SELECT COUNT(dp) "
                      + "FROM DocumentPart dp, Document d "
                      + "WHERE d.id = :documentId "
                      + "AND dp MEMBER OF d.content.parts "
                      + "GROUP BY dp.number "
                      + "ORDER BY dp.number")})
public class DocumentPartDao extends JpaDao<DocumentPart, String> {

  private static final Logger LOGGER = Logger.getLogger(DocumentPartDao.class.getName());

  private static final String DOCUMENT_PART_TITLE_PARAMETER = "partTitle";
  private static final String DOCUMENT_ID_PARAMETER = "documentId";

  /**
   * Creates a new data access object for DocumentParts using the given {@link EntityManager}.
   * 
   * @param em - the EntityManager to be used in the new DocumentPartDao
   */
  public DocumentPartDao(EntityManager em) {
    super(DocumentPart.class, em);
  }

  /**
   * Find all DocumentParts of a Document with the given id.
   * 
   * @param docId - the id of the Document
   * @return list of all DocumentParts of the Document
   */
  public List<DocumentPart> findPartsInDocument(String docId) {
    TypedQuery<DocumentPart> docQuery = em.createNamedQuery("DocumentPart.findPartsInDocument", 
                                                            DocumentPart.class);
    docQuery.setParameter(DOCUMENT_ID_PARAMETER, docId);
    return docQuery.getResultList();
  }
  
  /**
   * Finds the DocumentPart with the given title.
   * 
   * @param partTitle - the title of the DocumentPart to find
   * @return the DocumentPart with the given title
   */
  public DocumentPart findPartByTitle(String partTitle) {
    TypedQuery<DocumentPart> partQuery = em.createNamedQuery("DocumentPart.findPartByTitle",
                                                              DocumentPart.class);
    
    partQuery.setParameter(DOCUMENT_PART_TITLE_PARAMETER, partTitle);
    
    return partQuery.getSingleResult();
  }

  /**
   * Returns the number of parts in a Document with the given id.
   *
   * @param docId - the id of the Document to search in
   * @return number of DocumentParts
   */
  public int getNumberOfParts(String docId) {
    int number;
    Query countQuery = em.createNamedQuery("DocumentPart.getNumberOfPartsInDocument");
    countQuery.setParameter(DOCUMENT_ID_PARAMETER, docId);
    try {
      number = ((Number) countQuery.getSingleResult()).intValue();
    } catch (NoResultException nre) {
      LOGGER.log(Level.FINEST, "There are no parts in document with id " + docId + ".");
      number = 0;
    }
    return number;
  }

}
