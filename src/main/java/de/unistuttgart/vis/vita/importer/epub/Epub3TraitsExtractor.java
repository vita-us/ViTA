package de.unistuttgart.vis.vita.importer.epub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Resource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Epub3TraitsExtractor {

  private ContentBuilder contentBuilder = new ContentBuilder();

  public boolean existsSpan(Elements innerElements) {
    for (Element innerElement : innerElements) {
      if (innerElement.tagName().matches("span")) {
        return true;
      }
    }
    return false;
  }

  // Checks if ebook has parts
  public boolean existsPartInEpub3(List<Resource> resources) throws IOException {
    if (!(resources == null) && !resources.isEmpty()) {
      for (Resource resourceItem : resources) {
        if (resourceItem != null) {
          Document document =
              Jsoup.parse(contentBuilder.getStringFromInputStream(resourceItem.getInputStream()));
          Elements sections = document.select(Constants.SECTION);
          for (Element sectionItem : sections) {
            if (sectionItem.attr(Constants.EPUB_TYPE).toLowerCase().contains(Constants.EPUB3_PART)) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  public List<List<String>> extractChapters(List<Resource> resources) throws IOException {
    List<List<String>> chapters = new ArrayList<List<String>>();
    if (!(resources == null) && !resources.isEmpty()) {
      for (Resource resourceItem : resources) {
        if (resourceItem != null) {
          Document document =
              Jsoup.parse(contentBuilder.getStringFromInputStream(resourceItem.getInputStream()));
          Elements sections = document.select(Constants.SECTION);
          for (Element sectionItem : sections) {
            if (sectionItem.attr(Constants.EPUB_TYPE).toLowerCase().contains(Constants.CHAPTER)) {
              Elements chapterParagraphs = sectionItem.getAllElements();
              chapters.add(getChapterLines(chapterParagraphs));
            }
          }

        }
      }
    }
    return chapters;
  }

  private List<String> getChapterLines(Elements chapterParagraphs) {
    List<String> chapterLines = new ArrayList<String>();
    for (Element chapterParagraph : chapterParagraphs) {
      chapterLines.add(chapterParagraph.ownText());
    }
    return chapterLines;

  }

  public List<List<List<String>>> extractParts(List<Resource> resources) throws IOException {
    List<List<List<String>>> parts = new ArrayList<List<List<String>>>();
    if (!resources.isEmpty()) {
      for (Resource resourceItem : resources) {
        if (resourceItem != null) {
          Document document =
              Jsoup.parse(contentBuilder.getStringFromInputStream(resourceItem.getInputStream()));
          Elements sections = document.select(Constants.SECTION);
          for (Element sectionItem : sections) {
            if (sectionItem.attr(Constants.EPUB_TYPE).toLowerCase().contains(Constants.EPUB3_PART)) {
              parts.add(partBuilder(resourceItem, sectionItem, sections, resources));
            }
          }
        }
      }
    }
    return parts;
  }

  private List<List<String>> partBuilder(Resource newResource, Element newSection,
      Elements sections, List<Resource> resources) throws IOException {
    List<List<String>> partChapters = new ArrayList<List<String>>();

    // iterate through the current resource
    for (int i = sections.indexOf(newSection) + 1; i < sections.size(); i++) {

      if (sections.get(i).attr(Constants.EPUB_TYPE).toLowerCase().contains(Constants.CHAPTER)
          && !sections.get(i).attr(Constants.EPUB_TYPE).toLowerCase()
              .contains(Constants.EPUB3_PART)) {
        addChapterToPart(partChapters, sections.get(i));
      } else {
        return partChapters;
      }
    }

    // iterate through the remaining resources
    for (int j = resources.indexOf(newResource) + 1; j < resources.size(); j++) {
      Document document =
          Jsoup.parse(contentBuilder.getStringFromInputStream(resources.get(j).getInputStream()));
      sections = document.select(Constants.SECTION);
      for (Element sectionItem : sections) {
        if (sectionItem.attr(Constants.EPUB_TYPE).toLowerCase().contains(Constants.CHAPTER)
            && !sectionItem.attr(Constants.EPUB_TYPE).toLowerCase().contains(Constants.EPUB3_PART)) {
          addChapterToPart(partChapters, sectionItem);

        } else {
          return partChapters;
        }
      }
    }
    return partChapters;

  }

  private void addChapterToPart(List<List<String>> partChapters, Element section) {
    Elements chapterParagraphs = section.getAllElements();
    List<String> chapter = getChapterLines(chapterParagraphs);
    partChapters.add(chapter);
  }
}
