package de.unistuttgart.vis.vita.model.document;

import de.unistuttgart.vis.vita.analysis.AnalysisStatus;
import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;
import de.unistuttgart.vis.vita.model.progress.AnalysisProgress;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Represents an eBook file being imported into the software. Includes the id, metadata, metrics and
 * content of this eBook.
 */
@Entity
public class Document extends AbstractEntityBase {
  @Embedded
  private DocumentMetadata metadata;
  @Embedded
  private DocumentMetrics metrics;
  @Embedded
  private DocumentContent content;
  @OneToOne(cascade = CascadeType.ALL)
  private AnalysisProgress progress;

  @OneToOne(cascade = CascadeType.ALL)
  private AnalysisParameters parameters;

  private String filePath;
  
  @Column(length = 1000)
  private Date uploadDate;

  private String fileName;
  private UUID contentID;

  /**
   * Creates a new empty document, setting all fields to default values.
   */
  public Document() {
    this.metrics = new DocumentMetrics();
    this.content = new DocumentContent();
    this.metadata = new DocumentMetadata();
    this.progress = new AnalysisProgress();
    this.parameters = new AnalysisParameters();
    contentID = UUID.randomUUID();
  }

  /**
   * Copy the corresponding objects of the document into the new one.
   * The content id stays also the same which allows faster nlp analysis because of caching.
   * @param document The document to take the data from.
   * @return New Document object which can be modified for derive analysis.
   */
  public static Document copy(Document document) {
    Document newDoc = new Document();
    newDoc.setFileName(document.getFileName());
    newDoc.setFilePath(document.getFilePath());
    newDoc.setUploadDate(new Date());
    document.getProgress().setStatus(AnalysisStatus.READY);
    document.setMetadata(document.getMetadata());
    document.setContent(document.getContent());
    // This is important step to allow use of caching.
    document.setContentID(document.getContentID());
    document.setMetrics(document.getMetrics());

    return newDoc;
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
  
  /**
   * Gets the path to the uploaded file
   * @return the path, or null if the file does not exist anymore
   */
  public Path getFilePath() {
    if (filePath == null) {
      return null;
    }
    return Paths.get(filePath);
  }

  /**
   * Associates the path of the uploaded file with this document
   * @param filePath the path, or null to indicate that the file has been deleted
   */
  public void setFilePath(Path filePath) {
    this.filePath = filePath.toString();
  }

  /**
   * Gets the upload date to the uploaded file
   * @return the upload date to the uploaded file
   */
  public Date getUploadDate() {
    return uploadDate;
  }
  
  /**
   * Sets the upload date of the uploaded file
   * 
   * @param uploadDate
   */
  public void setUploadDate(Date uploadDate) {
    this.uploadDate = uploadDate;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public UUID getContentID() {
    return contentID;
  }

  public void setContentID(UUID contentID) {
    this.contentID = contentID;
  }

  /**
   * Gets the parameters that should be used in the analysis of this document
   * @return
   */
  public AnalysisParameters getParameters() {
    return parameters;
  }

  /**
   * Sets the parameters that should be used in the analysis of this document
   * @param parameters
   */
  public void setParameters(AnalysisParameters parameters) {
    this.parameters = parameters;
  }

  /**
   * Find the chapter for a given global offset.
   * @param globalOffset The offset to search the chapter.
   * @return
   */
  public Chapter getChapterAt(int globalOffset) {
    List<Chapter> allChapters = new ArrayList<>();

    for (DocumentPart documentPart : content.getParts()) {
      allChapters.addAll(documentPart.getChapters());
    }

    int lo = 0;
    int hi = allChapters.size() - 1;

    boolean toHigh = allChapters.get(hi).getRange().getEnd().getOffset() < globalOffset;
    boolean negative = globalOffset < 0;

    if (toHigh || negative) {
      throw new IndexOutOfBoundsException("Offset is not in range.");
    }

    while (lo <= hi) {
      int mid = lo + (hi - lo) / 2;
      Range range = allChapters.get(mid).getRange();
      int start = range.getStart().getOffset();
      int end = range.getEnd().getOffset();

      if (globalOffset < start) {
        hi = mid - 1;
      } else if (globalOffset > end) {
        lo = mid + 1;
      } else {
        return allChapters.get(mid);
      }
    }

    throw new IllegalStateException("Not found the correct chapter");
  }
}
