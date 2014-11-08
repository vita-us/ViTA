package de.unistuttgart.vis.vita.importer.epub;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.unistuttgart.vis.vita.importer.txt.util.Line;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;

public class MetadataBuilderEpub {

  private DocumentMetadata documentMetadata;
  private Calendar date = null;


  /**
   * Sets the documentMetadata object with default values
   */
  public MetadataBuilderEpub() {
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
  public DocumentMetadata getMetadata() {
    return documentMetadata;
  }
  
  /**
   * Set the title of the metadata.
   *
   * @param titleList List of Line - Contains all lines of the title.
   */
  public void setTitle(List<Line> titleList) {
    String metadataTitle = "";
    if (titleList != null) {
      metadataTitle = buildMetadataMultiline(titleList);
    }
    documentMetadata.setTitle(metadataTitle);
  }
  
  /**
   * Set the author of the metadata.
   *
   * @param authorList List of Line - Contains all lines of the author.
   */
  public void setAuthor(List<Line> authorList) {
    String metadataAuthor = "";
    if (authorList != null) {
      metadataAuthor = buildMetadataMultiline(authorList);
    }
    documentMetadata.setAuthor(metadataAuthor);
  }
  
  /**
   * Set the publish year of the metadata.
   *
   * @param publishYearList List of Line - Contains all lines of the publish year.
   */
  public void setPublishYear(List<Line> publishYearList) {
    String metadataPublishYear = "";
    if (publishYearList != null) {
      metadataPublishYear = buildMetadataMultiline(publishYearList);
      
    }
    setPublishYear(metadataPublishYear);

  }

  /**
   * Set the publisher of the metadata.
   *
   * @param publisherList List of Line - Contains all lines of the publisher.
   */
  public void setPublisher(List<Line> publisherList) {
    String metadataPublisher = "";
    if (publisherList != null) {
      metadataPublisher = buildMetadataMultiline(publisherList);
    }
    documentMetadata.setPublisher(metadataPublisher);
  }

  /**
   * Set the genre of the metadata.
   *
   * @param genreList List of Line - Contains all lines of the genre.
   */
  public void setGenre(List<Line> genreList) {
    String metadataGenre = "";
    if (genreList != null) {
      metadataGenre = buildMetadataMultiline(genreList);
    }
    documentMetadata.setGenre(metadataGenre);
  }

  /**
   * Set the edition of the metadata.
   *
   * @param editionList List of Line - Contains all lines of the edition.
   */
  public void setEdition(List<Line> editionList) {
    String metadataEdition = "";
    if (editionList != null) {
      metadataEdition = buildMetadataMultiline(editionList);
    }
    documentMetadata.setEdition(metadataEdition);
  }

  
  /**
   * Concatenates the multilines regarding the metadata and returns the complete metadata
   */
  private String buildMetadataMultiline(List<Line> newMetadataMultilineList) {
    String metadataLine = "";
    StringBuilder stringBuilder = new StringBuilder(metadataLine);

    for (Line metadataMultiLine : newMetadataMultilineList) {
      stringBuilder.append(" ");
      stringBuilder.append(metadataMultiLine.getText().trim());
    }
    metadataLine = stringBuilder.toString();
    metadataLine = metadataLine.trim();
    return metadataLine;
  }
  
  /**
   * Checks if the date format of the publisherYear is valid
   */
  private void setPublishYear(String newPublishYear) {

    List<SimpleDateFormat> dateFormats = new ArrayList<>();
    dateFormats.add(new SimpleDateFormat("yyyy", Locale.ENGLISH));
    dateFormats.add(new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH));
    dateFormats.add(new SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH));
    dateFormats.add(new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH));


    date = Calendar.getInstance();
    for (SimpleDateFormat dateFormat : dateFormats) {
      try {
        
        date.setTime(dateFormat.parse(newPublishYear));
        documentMetadata.setPublishYear(date.get(Calendar.YEAR));

      } catch (ParseException ex) {
        // do nothing
      }
    }
  }

}
