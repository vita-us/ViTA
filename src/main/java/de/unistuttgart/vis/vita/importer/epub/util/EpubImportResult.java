package de.unistuttgart.vis.vita.importer.epub.util;

import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

import java.util.List;

/**
 * Import Result for the EPub Import.
 */
public class EpubImportResult implements ImportResult {

  List<DocumentPart> parts;
  DocumentMetadata metadata;

  /**
   * Creates a new ImportResult providing the imported DocumentMetadata and a List of the imported
   * DocumentParts.
   *
   * @param parts    The parts of the document, containing the chapters.
   * @param metadata The metadata of the imported file.
   */
  public EpubImportResult(List<DocumentPart> parts, DocumentMetadata metadata) {
    super();
    this.parts = parts;
    this.metadata = metadata;
  }

  @Override
  public List<DocumentPart> getParts() {
    return parts;
  }

  @Override
  public DocumentMetadata getMetadata() {
    return metadata;
  }


}
