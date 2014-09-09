package de.unistuttgart.vis.vita.importer.txt;

import java.util.List;

/**
 * The MetadataBuilder builds the metadata values by editing the commited metadata of the MetadataAnalyzer
 * 
 *
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
  
  /**
   * 
   * @param newMetadataTitle The metadata to edit
   * @return the metadata value: metadataTitle
   */
  public String buildMetadataTitle(String newMetadataTitle) {
    String metadataTitle = null;
    if (newMetadataTitle != null) {
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
    return metadataTitle;

  }

  /**
   * 
   * @param newMetadataAuthor The metadata to edit
   * @return the metadata value: metadataAuthor
   */
  public String buildMetadataAuthor(String newMetadataAuthor) {
    String metadataAuthor = null;
    if (newMetadataAuthor != null) {
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
    return metadataAuthor;

  }

  /**
   * 
   * @param newMetadataPublishYear The metadata to edit
   * @return the metadata value: metadataPublishYear
   */
  public String buildMetadataPublishYear(String newMetadataPublishYear) {
    String metadataPublishYear = null;
    if (newMetadataPublishYear != null) {
      if (newMetadataPublishYear.contains(RELEASE_DATE_VERSION1)) {
        metadataPublishYear =
            newMetadataPublishYear.substring(newMetadataPublishYear
                .lastIndexOf(RELEASE_DATE_VERSION1) + RELEASE_DATE_VERSION1.length());
        metadataPublishYear = metadataPublishYear.trim();
      } else if (newMetadataPublishYear.contains(RELEASE_DATE_VERSION2)) {
        metadataPublishYear =
            newMetadataPublishYear.substring(newMetadataPublishYear
                .lastIndexOf(RELEASE_DATE_VERSION2) + RELEASE_DATE_VERSION2.length());
        metadataPublishYear = metadataPublishYear.trim();
      }
    }
    return metadataPublishYear;

  }

  /**
   * 
   * @param newMetadataPublisher The metadata to edit
   * @return the metadata value: metadataPublisher
   */
  public String buildMetadataPublisher(String newMetadataPublisher) {
    String metadataPublisher = null;
    if (newMetadataPublisher != null) {
      if (newMetadataPublisher.contains(PUBLISHER_VERSION1)) {
        metadataPublisher =
            newMetadataPublisher.substring(newMetadataPublisher.lastIndexOf(PUBLISHER_VERSION1)
                + PUBLISHER_VERSION1.length());
        metadataPublisher = metadataPublisher.trim();
      } else if (newMetadataPublisher.contains(PUBLISHER_VERSION2)) {
        metadataPublisher =
            newMetadataPublisher.substring(newMetadataPublisher.lastIndexOf(PUBLISHER_VERSION2)
                + PUBLISHER_VERSION2.length());
        metadataPublisher = metadataPublisher.trim();
      }
    }
    return metadataPublisher;

  }

  /**
   * 
   * @param newMetadataGenre The metadata to edit
   * @return the metadata value: metadataGenre
   */
  public String buildMetadataGenre(String newMetadataGenre) {
    String metadataGenre = null;
    if (newMetadataGenre != null) {
      if (newMetadataGenre.contains(GENRE_VERSION1)) {
        metadataGenre =
            newMetadataGenre.substring(newMetadataGenre.lastIndexOf(GENRE_VERSION1)
                + GENRE_VERSION1.length());
        metadataGenre = metadataGenre.trim();
      } else if (newMetadataGenre.contains(GENRE_VERSION2)) {
        metadataGenre =
            newMetadataGenre.substring(newMetadataGenre.lastIndexOf(GENRE_VERSION2)
                + GENRE_VERSION2.length());
        metadataGenre = metadataGenre.trim();
      }
    }
    return metadataGenre;

  }

  /**
   * 
   * @param newMetadataEdition The metadata to edit
   * @return the metadata value: metadataEdition
   */
  public String buildMetadataEdition(String newMetadataEdition) {
    String metadataEdition = null;
    if (newMetadataEdition != null) {
      if (newMetadataEdition.contains(EDITION_VERSION1)) {
        metadataEdition =
            newMetadataEdition.substring(newMetadataEdition.lastIndexOf(EDITION_VERSION1)
                + EDITION_VERSION1.length());
        metadataEdition = metadataEdition.trim();
      } else if (newMetadataEdition.contains(EDITION_VERSION2)) {
        metadataEdition =
            newMetadataEdition.substring(newMetadataEdition.lastIndexOf(EDITION_VERSION2)
                + EDITION_VERSION2.length());
        metadataEdition = metadataEdition.trim();
      }
    }
    return metadataEdition;

  }
  
  /**
   * Concatenates the multilines regarding the metadata and returns the complete metadata
   * @param newMetadataMultilineList
   * @return
   */
  public String buildMetadataMultiline(List<Line> newMetadataMultilineList){
    String metadataLine = "";
    StringBuilder stringBuilder = new StringBuilder(metadataLine);
    
    for(Line metadataMultiLine: newMetadataMultilineList){
      stringBuilder.append(" ");
      stringBuilder.append(metadataMultiLine.getText().trim());
    }
    metadataLine = stringBuilder.toString();
    return metadataLine;
  }
}
