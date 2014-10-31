package de.unistuttgart.vis.vita.importer.epub;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Date;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;

public class MetadataAnalyzerEpub {

  private static final String PUBLICATION = "publication";
  private static final String GENRE = "genre";
  private static final String EDITION = "edition";
  private static final String METADATA = "metadata";

  private Path path;
  private Book ebook;
  private ContentBuilder contentBuilder = new ContentBuilder();
  private Document document;

  public MetadataAnalyzerEpub(Book newEbook, Path newPath) {
    this.ebook = newEbook;
    this.path = newPath;
  }

  public DocumentMetadata extractMetadata() throws IOException, ParseException {
    DocumentMetadata documentMetadata = new DocumentMetadata();
    documentMetadata.setAuthor(getAuthor());
    documentMetadata.setEdition(getEdition());
    documentMetadata.setGenre(getGenre());
    documentMetadata.setPublisher(getPublisher());
    documentMetadata.setPublishYear(getPublishYear());
    documentMetadata.setTitle(getTitle());

    return documentMetadata;
  }

  private String getTitle() {
    String title = (new File(path.toString()).getName());
    if (ebook != null) {
      if (!ebook.getTitle().isEmpty()) {
        title = ebook.getTitle();
      }
    }
    return title;
  }

  private String getAuthor() {
    String author = "";
    if (!ebook.getMetadata().getAuthors().isEmpty()) {

      for (Author nameParts : ebook.getMetadata().getAuthors()) {
        author = nameParts.getFirstname() + " " + nameParts.getLastname();
      }
    }
    return author;
  }

  private String getPublisher() {
    String publisher = "";
    if (!ebook.getMetadata().getPublishers().isEmpty()) {
      if (ebook.getMetadata().getPublishers().size() > 1) {
        publisher = ebook.getMetadata().getPublishers().get(0);
        
        for (int i = 1; i < ebook.getMetadata().getPublishers().size(); i++) {
          publisher += ", "+ ebook.getMetadata().getPublishers().get(i);
        }
      }else{
        publisher = ebook.getMetadata().getPublishers().get(0);
      }
    }
    return publisher;

  }

  private int getPublishYear() throws ParseException {
    String publisherYear = null;
    Calendar date = Calendar.getInstance();
    try {
      if (!ebook.getMetadata().getDates().isEmpty()) {

        for (Date dateItem : ebook.getMetadata().getDates()) {
          if (dateItem.getEvent().toString().toLowerCase().matches(PUBLICATION)) {
            publisherYear = dateItem.getValue();
            date.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(publisherYear));
            break;
          }
        }
      }
    } catch (ParseException ex) {
      Logger log = Logger.getLogger("Exception");
      log.log(Level.SEVERE, "Incorrect date format", ex);
      return 0;
    }
    return date.get(Calendar.YEAR);

  }

  private String getGenre() throws IOException {
    String genre = "";
    document =
        Jsoup.parse(contentBuilder
            .getStringFromInputStream(ebook.getOpfResource().getInputStream()));
    Elements metadata = document.select(METADATA);

    for (Element metadataItem : metadata) {
      genre = extractMetadataByAttribute(genre, metadataItem, GENRE);
    }

    return genre;
  }

  private String getEdition() throws IOException {
    String edition = "";
    document =
        Jsoup.parse(contentBuilder
            .getStringFromInputStream(ebook.getOpfResource().getInputStream()));
    Elements metadata = document.select(METADATA);

    for (Element metadataItem : metadata) {
      edition = extractMetadataByAttribute(edition, metadataItem, EDITION);
    }
    return edition;
  }

  private String extractMetadataByAttribute(String metadata, Element metadataItem, String attribute) {

    for (Element metadataItemElement : metadataItem.getAllElements()) {
      if (metadataItemElement.tagName().toLowerCase().contains(attribute)) {
        metadata = metadataItemElement.text();
        break;
      }
    }
    return metadata;
  }

}
