package de.unistuttgart.vis.vita.importer.txt.analyzers;

import de.unistuttgart.vis.vita.importer.output.MetadataBuilder;
import de.unistuttgart.vis.vita.importer.util.Line;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;

import org.apache.commons.lang.StringUtils;


import java.util.ArrayList;
import java.util.List;

/**
 * The MetadataAnalyzer extracts the metadata of the commited metdataList and provides the
 * completely builded metadata.
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
  private List<Line> metadataList = new ArrayList<>();
  private String[] metadataStartArray = {"Title:", "TITLE:", "Author:", "AUTHOR:", "Release Date:",
      "RELEASE DATE:", "Publisher:", "PUBLISHER:", "Genre:", "GENRE:", "Edition:", "EDITION:",
      "Language:", "LANGUAGE:", "Last updated:", "LAST UPDATED:", "Illustrator:", "ILLUSTRATOR:",
      "Posting Date:", "POSTING DATE:"};

  /**
   * The commited lines and path will be used in the methods of this class
   * 
   * @param newMetadataList
   * @param newPath
   */
  public MetadataAnalyzer(List<Line> newMetadataList) {
    this.metadataList = newMetadataList;
  }

  /**
   * Extracts the metadata, which will be edited by the MetadataBuilder. The result will be saved in
   * documentMetadata.
   *
   * @return DocumentMetadata - The result of the analysis filled with all found information.
   */
  public DocumentMetadata extractMetadata() {
    MetadataBuilder metadataBuilder = new MetadataBuilder();
    if (!metadataList.isEmpty()) {
      for (Line line : metadataList) {
        if (line.getText().matches(TITLE)) {
          metadataBuilder.setTitle(buildMetadataTypeList(line));

        } else if (line.getText().matches(AUTHOR)) {
          metadataBuilder.setAuthor(buildMetadataTypeList(line));

        } else if (line.getText().matches(RELEASE_DATE)) {
          metadataBuilder.setPublishYear(buildMetadataTypeList(line));

        } else if (line.getText().matches(PUBLISHER)) {
          metadataBuilder.setPublisher(buildMetadataTypeList(line));

        } else if (line.getText().matches(GENRE)) {
          metadataBuilder.setGenre(buildMetadataTypeList(line));

        } else if (line.getText().matches(EDITION)) {
          metadataBuilder.setEdition(buildMetadataTypeList(line));
        }
      }
    }
    return metadataBuilder.getMetadata();
  }

  /**
   * Check if the current metadata is multiline
   */
  private boolean isMetadataMultiLine(Line newMetadataLine) {
    int nextIndex = metadataList.indexOf(newMetadataLine) + 1;
    if (nextIndex < metadataList.size()) {
      return !StringUtils.startsWithAny(metadataList.get(nextIndex).getText(), metadataStartArray)
          && !metadataList.get(nextIndex).getText().matches("^[\\s]*$");
    } else {
      return false;
    }
  }

  /**
   * Returns a list with the multilines regarding the metadata
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

  /**
   * Builds the list of one metadata type starting at the given line.
   *
   * @param line Line - The first line of the metadata type.
   * @return List of Line - A list containing all lines for one metadata type. At least the given
   *         line will be added, if there are more lines belongig to the type they will be added
   *         too.
   */
  private List<Line> buildMetadataTypeList(Line line) {
    List<Line> typeList = new ArrayList<>();
    typeList.add(line);
    if (isMetadataMultiLine(line)) {
      typeList.addAll(getMetadataMultilines(line));
    }
    return typeList;
  }
}
