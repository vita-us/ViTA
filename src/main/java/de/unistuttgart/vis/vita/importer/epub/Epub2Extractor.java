package de.unistuttgart.vis.vita.importer.epub;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.unistuttgart.vis.vita.importer.txt.util.ChapterPosition;
import de.unistuttgart.vis.vita.importer.txt.util.Line;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

public class Epub2Extractor extends AbstractEpubExtractor{

  private Document document;
  private ContentBuilder contentBuilder = new ContentBuilder();
  private EpubChapterBuilder epubChapterBuilder = new EpubChapterBuilder();
  private List<String> ids = new ArrayList<String>();

  public Epub2Extractor(Book book) throws IOException {
    super(book);
    addIds();
  }

  public de.unistuttgart.vis.vita.model.document.Document getDocument() throws IOException {

    de.unistuttgart.vis.vita.model.document.Document newDocument =
        new de.unistuttgart.vis.vita.model.document.Document();

    newDocument.getContent().getParts().add(extractChaptersEpub2());
    return newDocument;
  }

  private DocumentPart extractChaptersEpub2() throws IOException {
    List<Chapter> chapters = new ArrayList<Chapter>();
    List<Chapter> chaptersToRemove = new ArrayList<Chapter>();
    DocumentPart documentPart = new DocumentPart();

    for (Resource resource : resources) {
      document = Jsoup.parse(contentBuilder.getStringFromInputStream(resource.getInputStream()));
      for (String id : ids) {
        Element currentElement = document.getElementById(id);
        if (currentElement != null) {
          String chapter =
              StringUtils.join(extractChapters(currentElement, document, resource), "");
          chapters.add(epubChapterBuilder.buildChapter(chapter));
        }
      }
    }

    removeEmptyChapters(chapters, chaptersToRemove);
    documentPart.getChapters().addAll(chapters);
    return documentPart;
  }

  private void removeEmptyChapters(List<Chapter> chapters, List<Chapter> chaptersToRemove) {
    for (Chapter chapter : chapters) {
      if (chapter.getText().isEmpty()) {
        chaptersToRemove.add(chapter);
      }
    }
    chapters.removeAll(chaptersToRemove);
    chaptersToRemove.clear();
  }

  private List<String> extractChapters(Element currentElement, Document document,
      Resource currentResource) throws IOException {
    List<String> chaptersOfCurrentElement = new ArrayList<String>();

    // iterate through the current resource
    for (int i = document.getAllElements().indexOf(currentElement) + 1; i < document
        .getAllElements().size(); i++) {

      if (!ids.contains(document.getAllElements().get(i).id())) {
        if (!document.getAllElements().get(i).text().isEmpty()
            && document.getAllElements().get(i).tagName().equals("p")) {
          chaptersOfCurrentElement.add(document.getAllElements().get(i).text());
        }
      } else {
        return chaptersOfCurrentElement;
      }
    }

    // iterate through the remaining resources
    for (int j = resources.indexOf(currentResource) + 1; j < resources.size(); j++) {
      document =
          Jsoup.parse(contentBuilder.getStringFromInputStream(resources.get(j).getInputStream()));
      for (int k = 0; k < document.getAllElements().size(); k++) {

        if (!ids.contains(document.getAllElements().get(k).id())) {
          if (!document.getAllElements().get(k).text().isEmpty()
              && document.getAllElements().get(k).tagName().equals("p")) {
            chaptersOfCurrentElement.add(document.getAllElements().get(k).text());
          }
        } else {
          return chaptersOfCurrentElement;
        }
      }
    }
    return chaptersOfCurrentElement;
  }

  private void addIds() throws IOException {
    document = Jsoup.parse(contentBuilder.getStringFromInputStream(tocResource.getInputStream()));
    Elements navMaps = document.select("navMap");
    if (!navMaps.isEmpty()) {
      Elements contents = navMaps.get(0).select("content");
      if (!contents.isEmpty()) {
        for (Element content : contents) {
          if (content.hasAttr("src")) {
            String id = extractId(content.attr("src"));
            if (!ids.contains(id)) {
              ids.add(id);
            }
          }
        }
      }

    }
  }

  private String extractId(String input) {
    StringTokenizer stringTokenizer = new StringTokenizer(input, "#");
    while (stringTokenizer.hasMoreElements()) {
      stringTokenizer.nextToken();
      return stringTokenizer.nextToken();
    }
    return "";
  }

  @Override
  public List<List<Line>> getPartList() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<ChapterPosition> getChapterPositionList() {
    // TODO Auto-generated method stub
    return null;
  }

}
