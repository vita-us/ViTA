package de.unistuttgart.vis.vita.model.document;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;
import de.unistuttgart.vis.vita.model.progress.AnalysisProgress;

/**
 * Represents an eBook file being imported into the software. Includes the id, metadata, metrics and
 * content of this eBook.
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Document.findAllDocuments", query = "SELECT d " + "FROM Document d"),

    @NamedQuery(name = "Document.findDocumentById", query = "SELECT d " + "FROM Document d "
        + "WHERE d.id = :documentId"),

    @NamedQuery(name = "Document.findDocumentByTitle", query = "SELECT d " + "FROM Document d "
        + "WHERE d.metadata.title = :documentTitle")})
public class Document extends AbstractEntityBase {
  @Embedded
  private DocumentMetadata metadata;
  @Embedded
  private DocumentMetrics metrics;
  @Embedded
  private DocumentContent content;
  @OneToOne(cascade = CascadeType.ALL)
  private AnalysisProgress progress;

  /**
   * Creates a new empty document, setting all fields to default values.
   */
  public Document() {
    this.metrics = new DocumentMetrics();
    this.content = new DocumentContent();
    this.metadata = new DocumentMetadata();
    this.progress = new AnalysisProgress();
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

  /**
   * @return progress of the analysis of this document
   */
  public AnalysisProgress getProgress() {
    return progress;
  }

  /**
   * Sets the new progress of the analysis of this document.
   * 
   * @param newProgress - the new progress of the analysis of this document
   */
  public void setProgress(AnalysisProgress newProgress) {
    this.progress = newProgress;
  }

}
