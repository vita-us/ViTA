package de.unistuttgart.vis.vita.importer.txt.analyzers;

import java.io.File;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import de.unistuttgart.vis.vita.importer.txt.output.MetadataBuilder;
import de.unistuttgart.vis.vita.importer.txt.util.Line;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;

/**
 * The MetadataAnalyzer extract the metadata of the commited metdataList
 * 
 *
 */
public class MetadataAnalyzer {

  private static final String WHITESPACE = "([^\\S\\p{Graph}])*";
  private static final String TITLE = "^" + WHITESPACE + "((Title:)|(TITLE:)).+";
  private static final String AUTHOR = "^" + WHITESPACE + "((Author:)|(AUTHOR:)).+";
  private static final String RELEASE_DATE = "^" + WHITESPACE
      + "((Release Date:)|(RELEASE DATE:)).+";
  private static final String PUBLISHER = "^" + WHITESPACE + "((Publisher:)|(PUBLISHER:)).+";
  private static final String GENRE = "^" + WHITESPACE + "((Genre:)|(GENRE:)).+";
  private static final String EDITION = "^" + WHITESPACE + "((Edition:)|(EDITION:)).+";
  private List<Line> metadataList = new ArrayList<Line>();
  private String[] metadataStartArray = {"Title:", "TITLE:", "Author:", "AUTHOR:", "Release Date:",
      "RELEASE DATE:", "Publisher:", "PUBLISHER:", "Genre:", "GENRE:", "Edition:", "EDITION:"};
  private Path path;
  private Calendar date = null;
  private DocumentMetadata documentMetadata = new DocumentMetadata();

  public MetadataAnalyzer(List<Line> newMetadataList, Path newPath) {
    this.metadataList = newMetadataList;
    this.path = newPath;
    setDefaultValues();
  }

  /**
   * Extracts the metadata, which will be edited by the MetadataBuilder The result will be saved in
   * documentMetadata
   * 
   * @return documentMetadata
   */
  public DocumentMetadata extractMetadata() {
    MetadataBuilder metadataBuilder = new MetadataBuilder();
    if (!metadataList.isEmpty()) {
      for (Line line : metadataList) {
        if (line.getText().matches(TITLE)) {
          verifyTitleIsMultiline(metadataBuilder, line);

        } else if (line.getText().matches(AUTHOR)) {
          verifyAuthorIsMultiline(metadataBuilder, line);

        } else if (line.getText().matches(RELEASE_DATE)) {
          verifyPublisherYearIsMultiline(metadataBuilder, line);

        } else if (line.getText().matches(PUBLISHER)) {
          verifyPublisherIsMultiline(metadataBuilder, line);

        } else if (line.getText().matches(GENRE)) {
          verifyTGenreIsMultiline(metadataBuilder, line);

        } else if (line.getText().matches(EDITION)) {
          verifyEditionIsMultiline(metadataBuilder, line);
        }
      }
    } else {
      documentMetadata.setTitle(new File(path.toString()).getName());
    }
    return documentMetadata;
  }

  /**
   * Check if the current metadata line is multiline Extracts the metadata Publisher Year, if
   * multiline exits, with multilines
   * 
   * @param metadataBuilder
   * @param line
   */
  private void verifyPublisherYearIsMultiline(MetadataBuilder metadataBuilder, Line line) {
    String publisherYear;
    if (isMetadataMultiLine(line)) {
      publisherYear = line.getText().trim();
      publisherYear += metadataBuilder.buildMetadataMultiline(getMetadataMultilines(line));
      if (isValidPublisherYear(metadataBuilder.buildMetadataPublishYear(publisherYear))) {
        documentMetadata.setPublishYear(date.get(Calendar.YEAR));
      }
    } else {
      publisherYear = metadataBuilder.buildMetadataPublishYear(line.getText());
      if (isValidPublisherYear(publisherYear)) {
        documentMetadata.setPublishYear(date.get(Calendar.YEAR));
      }
    }
  }

  /**
   * Check if the current metadata line is multiline Extracts the metadata Edition, if multiline
   * exits, with multilines
   * 
   * @param metadataBuilder
   * @param line
   */
  private void verifyEditionIsMultiline(MetadataBuilder metadataBuilder, Line line) {
    String edition;
    if (isMetadataMultiLine(line)) {
      edition = line.getText().trim();
      edition += metadataBuilder.buildMetadataMultiline(getMetadataMultilines(line));
      documentMetadata.setEdition(metadataBuilder.buildMetadataEdition(edition));
    } else {
      edition = metadataBuilder.buildMetadataEdition(line.getText());
      documentMetadata.setEdition(edition);
    }
  }

  /**
   * Check if the current metadata line is multiline Extracts the metadata Genre, if multiline
   * exits, with multilines
   * 
   * @param metadataBuilder
   * @param line
   */
  private void verifyTGenreIsMultiline(MetadataBuilder metadataBuilder, Line line) {
    String genre;
    if (isMetadataMultiLine(line)) {
      genre = line.getText().trim();
      genre += metadataBuilder.buildMetadataMultiline(getMetadataMultilines(line));
      documentMetadata.setGenre(metadataBuilder.buildMetadataGenre(genre));
    } else {
      genre = metadataBuilder.buildMetadataGenre(line.getText());
      documentMetadata.setGenre(genre);
    }
  }

  /**
   * Check if the current metadata line is multiline Extracts the metadata Publisher, if multiline
   * exits, with multilines
   * 
   * @param metadataBuilder
   * @param line
   */
  private void verifyPublisherIsMultiline(MetadataBuilder metadataBuilder, Line line) {
    String publisher;
    if (isMetadataMultiLine(line)) {
      publisher = line.getText().trim();
      publisher += metadataBuilder.buildMetadataMultiline(getMetadataMultilines(line));
      documentMetadata.setPublisher(metadataBuilder.buildMetadataPublisher(publisher));
    } else {
      publisher = metadataBuilder.buildMetadataPublisher(line.getText());
      documentMetadata.setPublisher(publisher);
    }
  }

  /**
   * Check if the current metadata line is multiline Extracts the metadata Author, if multiline
   * exits, with multilines
   * 
   * @param metadataBuilder
   * @param line
   */
  private void verifyAuthorIsMultiline(MetadataBuilder metadataBuilder, Line line) {
    String author;
    if (isMetadataMultiLine(line)) {
      author = line.getText().trim();
      author += metadataBuilder.buildMetadataMultiline(getMetadataMultilines(line));
      documentMetadata.setAuthor(metadataBuilder.buildMetadataAuthor(author));
    } else {
      author = metadataBuilder.buildMetadataAuthor(line.getText());
      documentMetadata.setAuthor(author);
    }
  }

  /**
   * Check if the current metadata line is multiline
   * 
   * @param metadataBuilder
   * @param line
   */
  private void verifyTitleIsMultiline(MetadataBuilder metadataBuilder, Line line) {
    String title;
    if (isMetadataMultiLine(line)) {
      title = line.getText().trim();
      title += metadataBuilder.buildMetadataMultiline(getMetadataMultilines(line));
      documentMetadata.setTitle(metadataBuilder.buildMetadataTitle(title));
    } else {
      title = metadataBuilder.buildMetadataTitle(line.getText());
      documentMetadata.setTitle(title);
    }
  }

  /**
   * set the default values of documentMetadata
   */
  private void setDefaultValues() {
    documentMetadata.setAuthor("");
    documentMetadata.setEdition("");
    documentMetadata.setGenre("");
    documentMetadata.setPublisher("");
    documentMetadata.setPublishYear(0);
    documentMetadata.setTitle("");
  }

  /**
   * Checks if the date format of the publisherYear is valid
   * 
   * @param publisherYear
   * @return
   */
  private boolean isValidPublisherYear(String publisherYear) {
    List<SimpleDateFormat> dateFormats = new ArrayList<SimpleDateFormat>() {
      {
        add(new SimpleDateFormat("yyyy", Locale.ENGLISH));
        add(new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH));
        add(new SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH));
      }
    };

    for(SimpleDateFormat dateFormat: dateFormats){
      try{
        date = Calendar.getInstance();
        date.setTime(dateFormat.parse(publisherYear));
        return true;
      }catch(ParseException ex){
        date = null;
        Logger log = Logger.getLogger("Exception");
        log.log(Level.SEVERE, "Incorrect date format", ex);    
      }
    }
    return false;
  }
  
  /**
   * Check if the current metadata is multiline
   * 
   * @param newMetadataLine
   * @return
   */
  private boolean isMetadataMultiLine(Line newMetadataLine) {
    for (int i = metadataList.indexOf(newMetadataLine) + 1; i < metadataList.size(); i++) {
      if (!StringUtils.startsWithAny(metadataList.get(i).getText(), metadataStartArray)
          && !metadataList.get(i).getText().matches("^[\\s]*$")) {
        return true;
      } else {
        return false;
      }
    }
    return false;
  }

  /**
   * Returns a list with the multilines regarding the metadata
   * 
   * @param newMetadataLine
   * @return
   */
  private List<Line> getMetadataMultilines(Line newMetadataLine) {
    List<Line> metadataMultilineList = new ArrayList<Line>();
    metadataMultilineList.clear();
    int count = metadataList.indexOf(newMetadataLine) + 1;
    while (!StringUtils.startsWithAny(metadataList.get(count).getText(), metadataStartArray)
        && !metadataList.get(count).getText().matches("^[\\s]*$")) {
      metadataMultilineList.add(metadataList.get(count));
      count++;
    }
    return metadataMultilineList;
  }
}
