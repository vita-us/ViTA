package de.unistuttgart.vis.vita.importer.output;

import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

import java.util.List;

/**
 * Import Result for the TXT Import.
 */
public class ImportResultImpl implements ImportResult {

  List<DocumentPart> parts;
  DocumentMetadata metadata;
  private int totalLength;

  /**
   * Creates a new ImportResult providing the imported DocumentMetadata and a List of the imported
   * DocumentParts.
   *
   * @param parts    List of DocumentPart - the parts of the document, containing the chapters.
   * @param metadata DocumentMetadata - the metadata of the imported file.
   */
  public ImportResultImpl(List<DocumentPart> parts, DocumentMetadata metadata) {
    super();
    this.parts = parts;
    this.metadata = metadata;
    for (DocumentPart part : parts) {
      for (Chapter chapter : part.getChapters()) {
        totalLength += chapter.getLength();
      }
    }
  }

  @Override
  public List<DocumentPart> getParts() {
    return parts;
  }

  @Override
  public DocumentMetadata getMetadata() {
    return metadata;
  }

  @Override
  public int getTotalLength() {
    return totalLength;
  }
}
