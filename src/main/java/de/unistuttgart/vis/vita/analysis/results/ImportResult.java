package de.unistuttgart.vis.vita.analysis.results;

import de.unistuttgart.vis.vita.model.document.DocumentMetadata;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

import java.util.List;

/**
 * The result of importing a document
 */
public interface ImportResult {

  /**
   * Gets the parts (collections of chapters) in the document
   *
   * @return the parts
   */
  public List<DocumentPart> getParts();

  /**
   * Gets the meta data about the document
   *
   * @return the meta data
   */
  public DocumentMetadata getMetadata();
}
