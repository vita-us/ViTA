package de.unistuttgart.vis.vita.importer.epub;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Date;
import de.unistuttgart.vis.vita.importer.txt.output.MetadataBuilder;
import de.unistuttgart.vis.vita.importer.txt.util.Line;
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
    MetadataBuilder metadataBuilder = new MetadataBuilder();
    metadataBuilder.setAuthor(getAuthor());
    metadataBuilder.setEdition(getEdition());
    metadataBuilder.setGenre(getGenre());
    metadataBuilder.setPublisher(getPublisher());
    metadataBuilder.setPublishYear(getPublishYear());
    metadataBuilder.setTitle(getTitle());
    return metadataBuilder.getMetadata();
  }

  private List<Line> getTitle() {
    List<Line> titleList = new ArrayList<Line>();
    String title = new File(path.toString()).getName();
    if (ebook != null &&  !ebook.getTitle().isEmpty()) {
        title = ebook.getTitle();
    }
    titleList.add(new EpubModuleLine(title, false));
    return titleList;
  }

  private List<Line> getAuthor() {
    List<Line> author = new ArrayList<Line>();
    if (!ebook.getMetadata().getAuthors().isEmpty()) {
      for (Author nameParts : ebook.getMetadata().getAuthors()) {
        String authorName = nameParts.getFirstname() + " " + nameParts.getLastname() + ";";
        author.add(new EpubModuleLine(authorName, false));
      }
    }
    // remove ; from last author
    String lastAuthor = author.get(author.size() - 1).getText();
    author.get(author.size() - 1).setText(lastAuthor.substring(0, lastAuthor.length()));
    return author;
  }

  private List<Line> getPublisher() {
    List<Line> publisherList = new ArrayList<Line>();
    if (!ebook.getMetadata().getPublishers().isEmpty()) {
      for (int i = 0; i < ebook.getMetadata().getPublishers().size(); i++) {
        publisherList.add(new EpubModuleLine(ebook.getMetadata().getPublishers().get(i) + ";", false));
      }
    }
    // remove ; from last publisher
    String lastPublisher = publisherList.get(publisherList.size() - 1).getText();
    publisherList.get(publisherList.size() - 1).setText(lastPublisher.substring(0, lastPublisher.length()));
    return publisherList;

  }

  private List<Line> getPublishYear() throws ParseException {
    List<Line> publishYearList = new ArrayList<Line>();
      if (!ebook.getMetadata().getDates().isEmpty()) {

        for (Date dateItem : ebook.getMetadata().getDates()) {
          if (dateItem.getEvent().toString().toLowerCase().matches(PUBLICATION)) {
            publishYearList.add(new EpubModuleLine(dateItem.getValue(),false));
            break;
          }
        }
      }
    return publishYearList;

  }

  private List<Line> getGenre() throws IOException {
    String genre = "";
    document =
        Jsoup.parse(contentBuilder
            .getStringFromInputStream(ebook.getOpfResource().getInputStream()));
    Elements metadata = document.select(METADATA);

    for (Element metadataItem : metadata) {
      genre = extractMetadataByAttribute(genre, metadataItem, GENRE);
    }
    List<Line> genreList = new ArrayList<Line>();
    genreList.add(new EpubModuleLine(genre, false));
    return genreList;
  }

  private List<Line> getEdition() throws IOException {
    String edition = "";
    document =
        Jsoup.parse(contentBuilder
            .getStringFromInputStream(ebook.getOpfResource().getInputStream()));
    Elements metadata = document.select(METADATA);

    for (Element metadataItem : metadata) {
      edition = extractMetadataByAttribute(edition, metadataItem, EDITION);
    }
    List<Line> editionList = new ArrayList<Line>();
    editionList.add(new EpubModuleLine(edition, false));
    return editionList;
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
