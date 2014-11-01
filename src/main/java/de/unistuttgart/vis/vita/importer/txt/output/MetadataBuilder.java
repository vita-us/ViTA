package de.unistuttgart.vis.vita.importer.txt.output;

import de.unistuttgart.vis.vita.importer.txt.util.Line;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * The MetadataBuilder is the link between MetadataAnalyzer and the Metadata Objects used by all
 * other components of ViTA. It transforms the result of the MetadataAnalyzer into a Metadata. One
 * MetadataBuilder should be used for one Metadata.<br>
 */
public class MetadataBuilder {

  private static final String TITLE_VERSION1 = "Title:";
  private static final String TITLE_VERSION2 = "TITLE:";
  private static final String AUTHOR_VERSION1 = "Author:";
  private static final String AUTHOR_VERSION2 = "AUTHOR:";
  private static final String RELEASE_DATE_VERSION1 = "Release Date:";
  private static final String RELEASE_DATE_VERSION2 = "RELEASE DATE:";
  private static final String PUBLISHER_VERSION1 = "Publisher:";
  private static final String PUBLISHER_VERSION2 = "PUBLISHER:";
  private static final String GENRE_VERSION1 = "Genre:";
  private static final String GENRE_VERSION2 = "GENRE:";
  private static final String EDITION_VERSION1 = "Edition:";
  private static final String EDITION_VERSION2 = "EDITION:";
  private DocumentMetadata documentMetadata;
  private Calendar date = null;

  /**
   * Sets the documentMetadata object with default values
   */
  public MetadataBuilder() {
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
      String newMetadataTitle = buildMetadataMultiline(titleList);
      if (newMetadataTitle.contains(TITLE_VERSION1)) {
        metadataTitle =
            newMetadataTitle.substring(newMetadataTitle.lastIndexOf(TITLE_VERSION1)
                + TITLE_VERSION1.length());
        metadataTitle = metadataTitle.trim();
      } else if (newMetadataTitle.contains(TITLE_VERSION2)) {
        metadataTitle =
            newMetadataTitle.substring(newMetadataTitle.lastIndexOf(TITLE_VERSION2)
                + TITLE_VERSION2.length());
        metadataTitle = metadataTitle.trim();
      }
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
      String newMetadataAuthor = buildMetadataMultiline(authorList);
      if (newMetadataAuthor.contains(AUTHOR_VERSION1)) {
        metadataAuthor =
            newMetadataAuthor.substring(newMetadataAuthor.lastIndexOf(AUTHOR_VERSION1)
                + AUTHOR_VERSION1.length());
        metadataAuthor = metadataAuthor.trim();
      } else if (newMetadataAuthor.contains(AUTHOR_VERSION2)) {
        metadataAuthor =
            newMetadataAuthor.substring(newMetadataAuthor.lastIndexOf(AUTHOR_VERSION2)
                + AUTHOR_VERSION2.length());
        metadataAuthor = metadataAuthor.trim();
      }
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
      String newPublishYear = buildMetadataMultiline(publishYearList);
      if (newPublishYear.contains(RELEASE_DATE_VERSION1)) {
        metadataPublishYear =
            newPublishYear.substring(newPublishYear.lastIndexOf(RELEASE_DATE_VERSION1)
                + RELEASE_DATE_VERSION1.length());
        metadataPublishYear = metadataPublishYear.trim();
      } else if (newPublishYear.contains(RELEASE_DATE_VERSION2)) {
        metadataPublishYear =
            newPublishYear.substring(newPublishYear.lastIndexOf(RELEASE_DATE_VERSION2)
                + RELEASE_DATE_VERSION2.length());
        metadataPublishYear = metadataPublishYear.trim();
      }
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
      String newPublisher = buildMetadataMultiline(publisherList);
      if (newPublisher.contains(PUBLISHER_VERSION1)) {
        metadataPublisher =
            newPublisher.substring(newPublisher.lastIndexOf(PUBLISHER_VERSION1)
                + PUBLISHER_VERSION1.length());
        metadataPublisher = metadataPublisher.trim();
      } else if (newPublisher.contains(PUBLISHER_VERSION2)) {
        metadataPublisher =
            newPublisher.substring(newPublisher.lastIndexOf(PUBLISHER_VERSION2)
                + PUBLISHER_VERSION2.length());
        metadataPublisher = metadataPublisher.trim();
      }
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
      String newGenre = buildMetadataMultiline(genreList);
      if (newGenre.contains(GENRE_VERSION1)) {
        metadataGenre =
            newGenre.substring(newGenre.lastIndexOf(GENRE_VERSION1) + GENRE_VERSION1.length());
        metadataGenre = metadataGenre.trim();
      } else if (newGenre.contains(GENRE_VERSION2)) {
        metadataGenre =
            newGenre.substring(newGenre.lastIndexOf(GENRE_VERSION2) + GENRE_VERSION2.length());
        metadataGenre = metadataGenre.trim();
      }
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
      String newEdition = buildMetadataMultiline(editionList);
      if (newEdition.contains(EDITION_VERSION1)) {
        metadataEdition =
            newEdition.substring(newEdition.lastIndexOf(EDITION_VERSION1)
                + EDITION_VERSION1.length());
        metadataEdition = metadataEdition.trim();
      } else if (newEdition.contains(EDITION_VERSION2)) {
        metadataEdition =
            newEdition.substring(newEdition.lastIndexOf(EDITION_VERSION2)
                + EDITION_VERSION2.length());
        metadataEdition = metadataEdition.trim();
      }
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
