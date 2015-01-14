package de.unistuttgart.vis.vita.importer.epub.extractors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.unistuttgart.vis.vita.importer.epub.util.EpubModuleLine;
import de.unistuttgart.vis.vita.importer.output.MetadataBuilder;
import de.unistuttgart.vis.vita.importer.util.Line;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;

/**
 * Reads the metadata from the ePub-file and transforms it per MetadataBuilder into a
 * DocumentMetadata, which is the standard type for metadata in the ViTA-Project. If something can't
 * be read this class will behave robust, and return default or empty values.
 */
public class MetadataAnalyzerEpub {

  private static final Logger LOG = Logger.getLogger("Exception");
  private static final String PUBLICATION = "publication";
  private static final String GENRE = "genre";
  private static final String EDITION = "edition";
  private static final String METADATA = "metadata";

  private Book ebook;
  private ContentBuilder contentBuilder = new ContentBuilder();
  private Document document;

  /**
   * Will extract the metadata from the given book.
   * 
   * @param newEbook The ebook to extract the metadata from.
   * @param newPath The path to the eBook-file, the filename acts as alternative for the ebook's
   *        title.
   */
  public MetadataAnalyzerEpub(Book newEbook) {
    this.ebook = newEbook;
  }

  /**
   * Extracts the metadata from the ebook and returns the metadata..
   * 
   * @return the ebook's metadata.
   */
  public DocumentMetadata extractMetadata() {
    // use the metadata builder for the correct format
    MetadataBuilder metadataBuilder = new MetadataBuilder();
    metadataBuilder.setAuthor(getAuthor());
    metadataBuilder.setEdition(getEdition());
    metadataBuilder.setGenre(getGenre());
    metadataBuilder.setPublisher(getPublisher());
    metadataBuilder.setPublishYear(getPublishYear());
    metadataBuilder.setTitle(getTitle());
    return metadataBuilder.getMetadata();
  }

  /**
   * Get the ebook's title.
   * 
   * @return The title in line-representation.
   */
  private List<Line> getTitle() {
    List<Line> titleList = new ArrayList<Line>();
    // use file name...
    String title = "";
    // ... or better use title from the book, if it is possible.
    if (ebook != null && !ebook.getTitle().isEmpty()) {
      title = ebook.getTitle();
    }
    titleList.add(new EpubModuleLine(title, false));
    return titleList;
  }

  /**
   * Get the ebook's author.
   * 
   * @return The author in line-representation.
   */
  private List<Line> getAuthor() {
    List<Line> author = new ArrayList<Line>();
    if (!ebook.getMetadata().getAuthors().isEmpty()) {
      for (Author nameParts : ebook.getMetadata().getAuthors()) {
        String authorName = nameParts.getFirstname() + " " + nameParts.getLastname() + ";";
        author.add(new EpubModuleLine(authorName, false));
      }
      // remove ; from last author
      String lastAuthor = author.get(author.size() - 1).getText();
      author.get(author.size() - 1).setText(lastAuthor.substring(0, lastAuthor.length() - 1));
    }
    return author;
  }

  /**
   * Get the ebook's publisher.
   * 
   * @return The publisher in line-representation.
   */
  private List<Line> getPublisher() {
    List<Line> publisherList = new ArrayList<Line>();
    if (!ebook.getMetadata().getPublishers().isEmpty()) {
      for (int i = 0; i < ebook.getMetadata().getPublishers().size(); i++) {
        publisherList.add(new EpubModuleLine(ebook.getMetadata().getPublishers().get(i) + ";",
            false));
      }
      // remove ; from last publisher
      String lastPublisher = publisherList.get(publisherList.size() - 1).getText();
      publisherList.get(publisherList.size() - 1).setText(
          lastPublisher.substring(0, lastPublisher.length() - 1));
    } else {
      publisherList.add(new EpubModuleLine(""));
    }
    return publisherList;
  }

  /**
   * Get the ebook's publish year.
   * 
   * @return The publish year in line-representation.
   */
  private List<Line> getPublishYear() {
    List<Line> publishYearList = new ArrayList<Line>();
    if (!ebook.getMetadata().getDates().isEmpty()) {

      for (Date dateItem : ebook.getMetadata().getDates()) {
        if (dateItem.getEvent() != null) {
          if (dateItem.getEvent().toString().toLowerCase().matches(PUBLICATION)) {
            publishYearList.add(new EpubModuleLine(dateItem.getValue(), false));
            break;
          }
        }
      }
    }
    return publishYearList;
  }

  /**
   * Get the ebook's genre.
   * 
   * @return The genre in line-representation.
   */
  private List<Line> getGenre() {
    String genre = "";
    List<Line> genreList = new ArrayList<Line>();
    try {
      document =
          Jsoup.parse(contentBuilder.getStringFromInputStream(ebook.getOpfResource()
              .getInputStream()));
      Elements metadata = document.select(METADATA);

      for (Element metadataItem : metadata) {
        genre = extractMetadataByAttribute(metadataItem, GENRE);
      }
      genreList.add(new EpubModuleLine(genre, false));
    } catch (IOException e) {
      // Log and write empty data
      LOG.log(Level.WARNING, "Failed reading genre", e);
    }
    return genreList;
  }

  /**
   * Get the ebook's edition.
   * 
   * @return The edition in line-representation.
   */
  private List<Line> getEdition() {
    String edition = "";
    List<Line> editionList = new ArrayList<Line>();
    try {
      document =
          Jsoup.parse(contentBuilder.getStringFromInputStream(ebook.getOpfResource()
              .getInputStream()));
      Elements metadata = document.select(METADATA);

      for (Element metadataItem : metadata) {
        edition = extractMetadataByAttribute(metadataItem, EDITION);
      }
      editionList.add(new EpubModuleLine(edition, false));
    } catch (IOException e) {
      // Log and write empty data
      LOG.log(Level.WARNING, "Failed reading edition", e);
    }
    return editionList;
  }

  /**
   * Get the text of the first occurrence of the attribute out of the given element.
   * 
   * @param metadataItem The element in which the attribute should be foud.
   * @param attribute The attribute from which the text should be returned.
   * @return Text of the attribute found first.
   */
  private String extractMetadataByAttribute(Element metadataItem, String attribute) {
    String metadata = "";
    for (Element metadataItemElement : metadataItem.getAllElements()) {
      if (metadataItemElement.tagName().toLowerCase().contains(attribute)) {
        metadata = metadataItemElement.text();
        break;
      }
    }
    return metadata;
  }

}
