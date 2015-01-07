package de.unistuttgart.vis.vita.model.dao;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.document.DocumentPart;

/**
 * Represents a data access object for accessing DocumentParts.
 */
@ManagedBean
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
                    + "WHERE dp.title = :partTitle")})
public class DocumentPartDao extends JpaDao<DocumentPart, String> {

  private static final String DOCUMENTPART_TITLE_PARAMETER = "partTitle";
  private static final String DOCUMENT_ID_PARAMETER = "documentId";

  /**
   * Creates a new data access object for DocumentParts.
   */
  public DocumentPartDao() {
    super(DocumentPart.class);
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
    
    partQuery.setParameter(DOCUMENTPART_TITLE_PARAMETER, partTitle);
    
    return partQuery.getSingleResult();
  }

}
