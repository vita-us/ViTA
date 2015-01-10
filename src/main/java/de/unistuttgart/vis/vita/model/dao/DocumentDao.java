package de.unistuttgart.vis.vita.model.dao;

import de.unistuttgart.vis.vita.analysis.AnalysisStatus;
import de.unistuttgart.vis.vita.model.document.Document;

import javax.annotation.ManagedBean;
import javax.persistence.EntityManager;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TypedQuery;

import java.util.List;

/**
 * Represents a data access object for accessing documents.
 */
@ManagedBean @MappedSuperclass @NamedQueries({@NamedQuery(name = "Document.findAllDocuments",
    query = "SELECT d " + "FROM Document d " + "ORDER BY d.uploadDate DESC"),

    @NamedQuery(name = "Document.findDocumentById",
        query = "SELECT d " + "FROM Document d " + "WHERE d.id = :documentId"),

    @NamedQuery(name = "Document.findDocumentByTitle",
        query = "SELECT d " + "FROM Document d " + "WHERE d.metadata.title = :documentTitle"),

    @NamedQuery(name = "Document.findDocumentByStatus",
        query = "SELECT d " + "FROM Document d " + "WHERE d.progress.status = :status")})
public class DocumentDao extends JpaDao<Document, String> {

  private static final String DOCUMENT_TITLE_PARAMETER = "title";

  /**
   * Creates a new data access object for Documents.
   */
  public DocumentDao() {
    super(Document.class);
  }

  public DocumentDao(EntityManager em) {
    super(Document.class, em);
  }

  @Override public List<Document> findAll() {
    TypedQuery<Document> query = em.createNamedQuery("Document.findAllDocuments", Document.class);
    return query.getResultList();
  }

  /**
   * Finds a document with a given title.
   *
   * @param docTitle - the tile of the document to search for
   * @return the document with the given title
   */
  public Document findDocumentByTitle(String docTitle) {
    TypedQuery<Document> titleQuery =
        em.createNamedQuery("Document.findDocumentByTitle", Document.class);
    titleQuery.setParameter(DOCUMENT_TITLE_PARAMETER, docTitle);
    return titleQuery.getSingleResult();
  }

  /**
   * Finds a document with a given status.
   *
   * @param status the status to look for
   * @return the documents with the given status
   */
  public List<Document> findDocumentsByStatus(AnalysisStatus status) {
    TypedQuery<Document> query =
        em.createNamedQuery("Document.findDocumentByStatus", Document.class);
    query.setParameter("status", status);
    return query.getResultList();
  }

}
