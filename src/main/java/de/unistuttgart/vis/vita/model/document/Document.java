package de.unistuttgart.vis.vita.model.document;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Represents an eBook file being imported into the software. Includes the id, metadata, metrics and
 * content of this eBook.
 */
@Entity
public class Document {

  @GeneratedValue
  @Id
  private String id;
  
  private DocumentMetadata metadata;
  private DocumentMetrics metrics;
  private DocumentContent content;

  /**
   * Creates a new empty document, setting all fields to default values.
   */
  public Document() {
    this.metrics = new DocumentMetrics();
  }

  /**
   * Creates a new empty document with the given id.
   * 
   * @param pId - the id for the new Document
   */
  public Document(String pId) {
    this.id = pId;
  }

  /**
   * @return the id of this Document
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id of this Document.
   * 
   * @param newId - the new id for this document
   */
  public void setId(String newId) {
    this.id = newId;
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
