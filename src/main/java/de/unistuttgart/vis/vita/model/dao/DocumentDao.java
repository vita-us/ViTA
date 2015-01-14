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


  public DocumentDao(EntityManager em) {
    super(Document.class, em);
  }

  @Override public List<Document> findAll() {
    return queryAll("Document.findAllDocuments");
  }

  /**
   * Finds a document with a given title.
   *
   * @param docTitle - the tile of the document to search for
   * @return the document with the given title
   */
  public Document findDocumentByTitle(String docTitle) {
    return queryOne("Document.findDocumentByTitle",
        DOCUMENT_TITLE_PARAMETER, docTitle);
  }

  /**
   * Finds a document with a given status.
   *
   * @param status the status to look for
   * @return the documents with the given status
   */
  public List<Document> findDocumentsByStatus(AnalysisStatus status) {
    return queryAll("Document.findDocumentByStatus",
        "status", status);
  }

}
