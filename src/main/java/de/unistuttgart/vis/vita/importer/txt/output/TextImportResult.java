package de.unistuttgart.vis.vita.importer.txt.output;

import java.util.List;

import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

/**
 * Import Result for the TXT Import.
 */
public class TextImportResult implements ImportResult {
  List<DocumentPart> parts;
  DocumentMetadata metadata;

  /**
   * Creates a new ImportResult providing the imported DocumentMetadata and a List of the imported
   * DocumentParts.
   * 
   * @param parts List of DocumentPart - the parts of the document, containing the chapters.
   * @param metadata DocumentMetadata - the metadata of the imported file.
   */
  public TextImportResult(List<DocumentPart> parts, DocumentMetadata metadata) {
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
