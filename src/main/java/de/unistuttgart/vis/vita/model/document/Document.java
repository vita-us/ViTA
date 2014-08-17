package de.unistuttgart.vis.vita.model.document;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Represents an eBook file being imported into the software. Includes the id, metadata, metrics and
 * content of this eBook.
 */
@Entity
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
      + "WHERE d.metadata.title = :documentTitle")
})
public class Document {

  @GeneratedValue
  @Id
  private String id;

  @Embedded
  private DocumentMetadata metadata;
  @Embedded
  private DocumentMetrics metrics;
  @Embedded
  private DocumentContent content;

  /**
   * Creates a new empty document, setting all fields to default values.
   */
  public Document() {
    this.metrics = new DocumentMetrics();
    this.content = new DocumentContent();
    this.metadata = new DocumentMetadata();
  }

  /**
   * @return the id of this Document
   */
  public String getId() {
    return id;
  }

  /**
   * @return the meta data for this document
   */
  public DocumentMetadata getMetadata() {
    return metadata;
  }

  /**
   * Sets the meta data for this document.
   * 
   * @param newMetadata - the meta data for this document
   */
  public void setMetadata(DocumentMetadata newMetadata) {
    this.metadata = newMetadata;
  }

  /**
   * @return object holding the metrics about this document.
   */
  public DocumentMetrics getMetrics() {
    return metrics;
  }

  /**
   * Sets the metrics for this Document.
   * 
   * @param newMetrics - the metrics for this Document
   */
  public void setMetrics(DocumentMetrics newMetrics) {
    this.metrics = newMetrics;
  }

  /**
   * @return the content of this document including the text and entities
   */
  public DocumentContent getContent() {
    return content;
  }

  /**
   * Sets the content for this Document.
   * 
   * @param content - the document content, including text and entities
   */
  public void setContent(DocumentContent content) {
    this.content = content;
  }

}
