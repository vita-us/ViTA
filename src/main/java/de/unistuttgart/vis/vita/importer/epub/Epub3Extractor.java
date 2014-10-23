package de.unistuttgart.vis.vita.importer.epub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import nl.siegmann.epublib.domain.Resource;

public class Epub3Extractor {

  private List<Resource> resources;
  private org.jsoup.nodes.Document document;
  private ContentBuilder contentBuilder = new ContentBuilder();
  private EpubChapterBuilder epubChapterBuilder = new EpubChapterBuilder();
  private Elements sections;

  public Epub3Extractor(List<Resource> newResources) {
    this.resources = newResources;
  }

  public Document getDocument() throws IOException {
    Document newDocument = new Document();
    if (existsPartInEpub3()) {
      newDocument.getContent().getParts().addAll(getPartsEpub3());
    } else {
      newDocument.getContent().getParts().add(getPartEpub3());
    }
    return newDocument;
  }

  //Checks if ebook has parts
  private boolean existsPartInEpub3() throws IOException {
    if (!resources.isEmpty()) {
      for (Resource resourceItem : resources) {
        if (resourceItem != null) {
          document =
              Jsoup.parse(contentBuilder.getStringFromInputStream(resourceItem.getInputStream()));
          sections = document.select("section");
          for (Element sectionItem : sections) {
            if (sectionItem.attr("epub:type").toLowerCase().contains("part")) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  
  private DocumentPart getPartEpub3() throws IOException {
    DocumentPart documentPart = new DocumentPart();
    List<Chapter> chapters = new ArrayList<Chapter>();
    if (!resources.isEmpty()) {
      for (Resource resourceItem : resources) {
        if (resourceItem != null) {
          document =
              Jsoup.parse(contentBuilder.getStringFromInputStream(resourceItem.getInputStream()));
          Elements sections = document.select("section");
          for (Element sectionItem : sections) {
            if (sectionItem.attr("epub:type").toLowerCase().contains("chapter")) {
              Elements chapterParagraphs = sectionItem.getElementsByTag("p");
              String chapter = chapterParagraphs.text();
              chapters.add(epubChapterBuilder.buildChapter(chapter));
            }
          }

        }
      }
    }
    documentPart.getChapters().addAll(chapters);
    return documentPart;
  }

  private List<DocumentPart> getPartsEpub3() throws IOException {
    List<DocumentPart> parts = new ArrayList<DocumentPart>();
    if (!resources.isEmpty()) {
      for (Resource resourceItem : resources) {
        if (resourceItem != null) {
          document =
              Jsoup.parse(contentBuilder.getStringFromInputStream(resourceItem.getInputStream()));
          sections = document.select("section");
          for (Element sectionItem : sections) {
            if (sectionItem.attr("epub:type").toLowerCase().contains("part")) {
              DocumentPart documentPart = new DocumentPart();
              documentPart.getChapters().addAll(partBuilder(resourceItem, sectionItem));
              parts.add(documentPart);
            }
          }
        }
      }
    }
    return parts;
  }

  private List<Chapter> partBuilder(Resource newResource, Element newSection) throws IOException {
    List<Chapter> partsChapters = new ArrayList<Chapter>();

    // iterate through the current resource
    for (int i = sections.indexOf(newSection) + 1; i < sections.size(); i++) {

      if (sections.get(i).attr("epub:type").toLowerCase().contains("chapter")
          && !sections.get(i).attr("epub:type").toLowerCase().contains("part")) {
        addChapterToPart(partsChapters, sections.get(i));
      } else {
        return partsChapters;
      }
    }

    // iterate through the remaining resources
    for (int j = resources.indexOf(newResource) + 1; j < resources.size(); j++) {
      document =
          Jsoup.parse(contentBuilder.getStringFromInputStream(resources.get(j).getInputStream()));
      sections = document.select("section");
      for (Element sectionItem : sections) {
        if (sectionItem.attr("epub:type").toLowerCase().contains("chapter")
            && !sectionItem.attr("epub:type").toLowerCase().contains("part")) {
          addChapterToPart(partsChapters, sectionItem);

        } else {
          return partsChapters;
        }
      }
    }
    return partsChapters;

  }

  private void addChapterToPart(List<Chapter> parts, Element section) {
    Elements chapterParagraphs = section.getElementsByTag("p");
    String chapter = chapterParagraphs.text();
    parts.add(epubChapterBuilder.buildChapter(chapter));
  }

}
