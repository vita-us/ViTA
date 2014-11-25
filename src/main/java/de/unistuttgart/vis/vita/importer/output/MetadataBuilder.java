package de.unistuttgart.vis.vita.importer.output;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.unistuttgart.vis.vita.importer.util.Line;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;

/**
 * The MetadataBuilder is the link between MetadataAnalyzer and the Metadata Objects used by all
 * other components of ViTA. It transforms the result of the MetadataAnalyzer into a Metadata. One
 * MetadataBuilder should be used for one Metadata.<br>
 */

//TODO issue 138
public class MetadataBuilder {

  private static final String TITLE_VERSION1 = "Title:";
  private static final String AUTHOR_VERSION1 = "Author:";
  private static final String RELEASE_DATE_VERSION1 = "Release Date:";
  private static final String PUBLISHER_VERSION1 = "Publisher:";
  private static final String GENRE_VERSION1 = "Genre:";
  private static final String EDITION_VERSION1 = "Edition:";
  private final Set<String> titleSet = new HashSet<String>();
  private final Set<String> authorSet = new HashSet<String>();
  private final Set<String> releaseDateSet = new HashSet<String>();
  private final Set<String> publisherSet = new HashSet<String>();
  private final Set<String> genreSet = new HashSet<String>();
  private final Set<String> editionSet = new HashSet<String>();
  private DocumentMetadata documentMetadata;


  private Calendar date = null;

  /**
   * Sets the documentMetadata object with default values
   */
  public MetadataBuilder() {
    super();

    documentMetadata = new DocumentMetadata();
    documentMetadata.setAuthor("");
    documentMetadata.setEdition("");
    documentMetadata.setGenre("");
    documentMetadata.setPublisher("");
    documentMetadata.setPublishYear(0);
    documentMetadata.setTitle("");

    titleSet.add(TITLE_VERSION1);
    authorSet.add(AUTHOR_VERSION1);
    releaseDateSet.add(RELEASE_DATE_VERSION1);
    publisherSet.add(PUBLISHER_VERSION1);
    genreSet.add(GENRE_VERSION1);
    editionSet.add(EDITION_VERSION1);
  }

  /**
   * Get metadata filled with all added information or default values.
   * 
   * @return The metadata
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
    documentMetadata.setTitle(filterMetadata(titleList, titleSet));
  }

  /**
   * Set the author of the metadata.
   *
   * @param authorList List of Line - Contains all lines of the author.
   */
  public void setAuthor(List<Line> authorList) {
    documentMetadata.setAuthor(filterMetadata(authorList, authorSet));
  }

  /**
   * Set the publish year of the metadata.
   *
   * @param publishYearList List of Line - Contains all lines of the publish year.
   */
  public void setPublishYear(List<Line> publishYearList) {
    setPublishYear(filterMetadata(publishYearList, releaseDateSet));

  }

  /**
   * Set the publisher of the metadata.
   *
   * @param publisherList List of Line - Contains all lines of the publisher.
   */
  public void setPublisher(List<Line> publisherList) {
    documentMetadata.setPublisher(filterMetadata(publisherList, publisherSet));
  }

  /**
   * Set the genre of the metadata.
   *
   * @param genreList List of Line - Contains all lines of the genre.
   */
  public void setGenre(List<Line> genreList) {
    documentMetadata.setGenre(filterMetadata(genreList, genreSet));
  }

  /**
   * Set the edition of the metadata.
   *
   * @param editionList List of Line - Contains all lines of the edition.
   */
  public void setEdition(List<Line> editionList) {
    documentMetadata.setEdition(filterMetadata(editionList, editionSet));
  }

  /**
   * Some text at the beginning of the metadata or white spaces at the beginning and end of the raw
   * metadata will be deleted.
   * 
   * @param metadata The raw metadata in list-representation
   * @param preTexts Describes which text at the beginning of the metadata belongs not the metadata
   *        itself e.g. 'title:'. You should not add text which is the start of another text,
   *        because there will be only one reduction.
   * @return The formated metadata in String-representation
   */
  private String filterMetadata(List<Line> metadata, Set<String> preTexts) {
    String text = "";
    if (metadata != null) {
      String newMetadata = buildMetadataMultiline(metadata);
      newMetadata = newMetadata.trim();
      // Search for text to reduce, not case sensitive
      for (String preText : preTexts) {
        if (newMetadata.toLowerCase().startsWith(preText.toLowerCase())) {
          newMetadata = newMetadata.substring(preText.length());
          newMetadata = newMetadata.trim();
          break;
        }
      }
      text = newMetadata;
    }
    return text;
  }

  /**
   * Checks if the date format of the publisherYear is valid
   */
  private void setPublishYear(String newPublishYear) {

    List<SimpleDateFormat> dateFormats = new ArrayList<>();
    dateFormats.add(new SimpleDateFormat("yyyy", Locale.ENGLISH));
    dateFormats.add(new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH));
    dateFormats.add(new SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH));
    dateFormats.add(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH));


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
}
