package de.unistuttgart.vis.vita.model.dao;

import de.unistuttgart.vis.vita.analysis.AnalysisStatus;
import de.unistuttgart.vis.vita.model.document.Document;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Represents a data access object for accessing documents.
 */
@MappedSuperclass
@NamedQueries({
    @NamedQuery(name = "Document.findAllDocuments",
        query = "SELECT d "
                + "FROM Document d "
                + "ORDER BY d.uploadDate DESC"),

    @NamedQuery(name = "Document.findDocumentById",
        query = "SELECT d "
                + "FROM Document d "
                + "WHERE d.id = :documentId"),

    @NamedQuery(name = "Document.findDocumentByTitle",
        query = "SELECT d "
                + "FROM Document d "
                + "WHERE d.metadata.title = :documentTitle"),

    @NamedQuery(name = "Document.findDocumentsByFilename",
        query = "SELECT d " + "FROM Document d "
                + "WHERE d.fileName = :fileName"),

    @NamedQuery(name = "Document.findDocumentByStatus",
        query = "SELECT d "
                + "FROM Document d "
                + "WHERE d.progress.status = :status")
})
public class DocumentDao extends JpaDao<Document, String> {

  private static final String DOCUMENT_TITLE_PARAMETER = "title";

  /**
   * Creates a new data access object to access Documents.
   *
   * @param em - the EntityManager to be used
   */
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
    return queryOne("Document.findDocumentByTitle", DOCUMENT_TITLE_PARAMETER, docTitle);
  }

  /**
   * Finds a document with a given status.
   *
   * @param status the status to look for
   * @return the documents with the given status
   */
  public List<Document> findDocumentsByStatus(AnalysisStatus status) {
    return queryAll("Document.findDocumentByStatus", "status", status);
  }

  /**
   * Returns whether the analysis of the Document with the given id is finished.
   *
   * @param documentId - the id of the Document which analysis status should be checked
   * @return true if the analysis for the given Document is finished, false otherwise
   */
  public boolean isAnalysisFinished(String documentId) {
    return findById(documentId).getProgress().getStatus() == AnalysisStatus.FINISHED;
  }

  /**
   * Finds all documents with the given file name.
   *
   * @param fileName The name of the file corresponding to the document.
   * @return the documents with the given filename.
   */
  public List<Document> findDocumentsByFilename(String fileName) {
    return queryAll("Document.findDocumentsByFilename", "fileName", fileName);
  }

}
