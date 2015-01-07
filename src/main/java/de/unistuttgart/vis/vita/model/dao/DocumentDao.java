package de.unistuttgart.vis.vita.model.dao;

import javax.annotation.ManagedBean;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TypedQuery;

import de.unistuttgart.vis.vita.model.document.Document;

/**
 * Represents a data access object for accessing documents.
 */
@ManagedBean
@MappedSuperclass
@NamedQueries({
  @NamedQuery(name = "Document.findAllDocuments",
              query = "SELECT d "
                    + "FROM Document d"),

  @NamedQuery(name = "Document.findDocumentById",
              query = "SELECT d "
                    + "FROM Document d "
                    + "WHERE d.id = :documentId"),

  @NamedQuery(name = "Document.findDocumentByTitle",
              query = "SELECT d "
                    + "FROM Document d "
                    + "WHERE d.metadata.title = :documentTitle")})
public class DocumentDao extends JpaDao<Document, String> {

  private static final String DOCUMENT_TITLE_PARAMETER = "title";

  /**
   * Creates a new data access object for Documents.
   */
  public DocumentDao() {
    super(Document.class);
  }

  /**
   * Finds a document with a given title.
   * 
   * @param docTitle - the tile of the document to search for
   * @return the document with the given title
   */
  public Document findDocumentByTitle(String docTitle) {
    TypedQuery<Document> titleQuery = em.createNamedQuery("Document.findDocumentByTitle", 
                                                          Document.class);
    titleQuery.setParameter(DOCUMENT_TITLE_PARAMETER, docTitle);
    return titleQuery.getSingleResult();
  }
  
}
