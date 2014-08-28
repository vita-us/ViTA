package de.unistuttgart.vis.vita.importer.txt;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FilenameUtils;

import de.unistuttgart.vis.vita.model.document.DocumentMetadata;

/**
 * The MetadataAnalyzer extract the metadata of the commited metdataList
 * 
 *
 */
public class MetadataAnalyzer {

  private static final String TITLE = "^((Title:)|(TITLE:)).+";
  private static final String AUTHOR = "^((Author:)|(AUTHOR:)).+";
  private static final String RELEASE_DATE = "^((Release Date:)|(RELEASE DATE:)).+([0-9]{4}).*";
  private static final String PUBLISHER = "^((Publisher:)|(PUBLISHER:)).+";
  private static final String GENRE = "^((Genre:)|(GENRE:)).+";
  private static final String EDITION = "^((Edition:)|(EDITION:)).+";
  private List<Line> metadataList = new ArrayList<Line>();
  private String path;
  private Date date = null;
  private DocumentMetadata documentMetadata = new DocumentMetadata();
  
  public MetadataAnalyzer(List<Line> newMetadataList, String newPath) {
    this.metadataList = newMetadataList;
    this.path = newPath;

  }

  /**
   * Extracts the metadata, which will be edited by the MetadataBuilder
   * The result will be saved in documentMetadata
   * @return documentMetadata 
   */
  public DocumentMetadata extractMetadata() {
    MetadataBuilder metadataBuilder = new MetadataBuilder();
    setDefaultValues();
    if (!metadataList.isEmpty()) {
      for (Line line : metadataList) {
        if (line != null) {
          if (line.getText().matches(TITLE)) {
            String title = metadataBuilder.buildMetadataTitle(line.getText());
            documentMetadata.setTitle(title);

          } else if (line.getText().matches(AUTHOR)) {
            String author = metadataBuilder.buildMetadataAuthor(line.getText());
            documentMetadata.setAuthor(author);

          } else if (line.getText().matches(RELEASE_DATE)) {
            String publisherYear = metadataBuilder.buildMetadataPublishYear(line.getText());
            if (isValidPublisherYear(publisherYear)) {
              documentMetadata.setPublishYear(date);
            }

          } else if (line.getText().matches(PUBLISHER)) {
            String publisher = metadataBuilder.buildMetadataPublisher(line.getText());
            documentMetadata.setPublisher(publisher);

          } else if (line.getText().matches(GENRE)) {
            String genre = metadataBuilder.buildMetadataGenre(line.getText());
            documentMetadata.setGenre(genre);

          } else if (line.getText().matches(EDITION)) {
            String edition = metadataBuilder.buildMetadataEdition(line.getText());
            documentMetadata.setEdition(edition);
          }
        }
      }
    } else {

      documentMetadata.setTitle(new File(path).getName());
    }
    return documentMetadata;
  }

  /**
   * set the default values of documentMetadata
   */
  public void setDefaultValues() {
    documentMetadata.setAuthor("");
    documentMetadata.setEdition("");
    documentMetadata.setGenre("");
    documentMetadata.setPublisher("");
    documentMetadata.setPublishYear(date);
    documentMetadata.setTitle("");
  }

  /**
   * Checks if the date format of the publisherYear is valid
   * @param publisherYear
   * @return
   */
  public boolean isValidPublisherYear(String publisherYear) {
    try {
      date = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(publisherYear);
    } catch (ParseException ex) {
      ex.printStackTrace();
    }
    return date != null;

  }
}
