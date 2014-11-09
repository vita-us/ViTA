package de.unistuttgart.vis.vita.importer.epub;

import java.util.List;

import de.unistuttgart.vis.vita.importer.txt.util.Line;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;

/**
 * The abstract MetadataBuilderEpub sets the correct value of the DocumentMetadata attributes
 */
public abstract class AbstractMetadataBuilderEpub {

  protected DocumentMetadata documentMetadata;

  /**
   * Sets the documentMetadata object with default values
   */
  public AbstractMetadataBuilderEpub() {
    documentMetadata = new DocumentMetadata();
    documentMetadata.setAuthor("");
    documentMetadata.setEdition("");
    documentMetadata.setGenre("");
    documentMetadata.setPublisher("");
    documentMetadata.setPublishYear(0);
    documentMetadata.setTitle("");
  }
  
  /**
   * Returns the Metadata with the currently set information.
   *
   * @return DocumentMetadata - The Metadata with all set information.
   */
  public abstract DocumentMetadata getMetadata();
  
  /**
   * Set the title of the metadata.
   *
   * @param titleList List of Line - Contains all lines of the title.
   */
  public abstract void setTitle(List<Line> titleList);
  
  /**
   * Set the author of the metadata.
   *
   * @param authorList List of Line - Contains all lines of the author.
   */
  public abstract void setAuthor(List<Line> authorList);
  
  /**
   * Set the publish year of the metadata.
   *
   * @param publishYearList List of Line - Contains all lines of the publish year.
   */
  public abstract void setPublishYear(List<Line> publishYearList);
  
  /**
   * Set the publisher of the metadata.
   *
   * @param publisherList List of Line - Contains all lines of the publisher.
   */
  public abstract void setPublisher(List<Line> publisherList);
  
  /**
   * Set the genre of the metadata.
   *
   * @param genreList List of Line - Contains all lines of the genre.
   */
  public abstract void setGenre(List<Line> genreList);
  
  /**
   * Set the edition of the metadata.
   *
   * @param editionList List of Line - Contains all lines of the edition.
   */
  public abstract void setEdition(List<Line> editionList);
}
