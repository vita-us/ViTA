package de.unistuttgart.vis.vita.importer.epub.extractors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Resource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Extracts various traits regarding Epub3
 * 
 *
 */
public class Epub3TraitsExtractor {

  private ContentBuilder contentBuilder = new ContentBuilder();
  private PartsAndChaptersReviser reviser = new PartsAndChaptersReviser();


  /**
   * Checks if the book has part
   * 
   * @param resources
   * @return
   * @throws IOException
   */
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
  
  /**
   * Extracts chapters of the book
   * 
   * @param resources
   * @return
   * @throws IOException
   */
  public List<List<Epubline>> extractChapters(List<Resource> resources) throws IOException {
    List<List<Epubline>> chapters = new ArrayList<List<Epubline>>();
    if (!(resources == null) && !resources.isEmpty()) {
      for (Resource resourceItem : resources) {
        if (resourceItem != null) {
          Document document =
              Jsoup.parse(contentBuilder.getStringFromInputStream(resourceItem.getInputStream()));
          Elements sections = document.select(Constants.SECTION);
          for (Element sectionItem : sections) {
            if (sectionItem.attr(Constants.EPUB_TYPE).toLowerCase().contains(Constants.CHAPTER)) {
              Elements chapterElements = sectionItem.getAllElements();
              chapterElements.remove(0);
              chapters.add(getChapterLines(chapterElements));
            }
          }

        }
      }
    }
    return chapters;
  }

  /**
   * Adds the text of the elements in section to List
   * 
   * @param chapterElements
   * @return
   * @throws IOException
   */
  public List<Epubline> getChapterLines(Elements chapterElements) throws IOException {
    List<Epubline> chapter = new ArrayList<Epubline>();
    List<Element> editedElements = new ArrayList<Element>();
    for (Element chapterElement : chapterElements) {
      if (!reviser.elementEdited(editedElements, chapterElement)) {
        if (!chapterElement.tagName().matches(Constants.SPAN) && !chapterElement.tagName().matches(Constants.DIV)) {
          boolean existsSpan = reviser.existsSpan(chapterElement);
          reviser.addText(chapter, chapterElement, existsSpan, "");
        } else if (chapterElement.tagName().matches(Constants.DIV)) {
          reviser.addDivTexts(chapter, chapterElement, editedElements, "");
        }
      }
    }
    return chapter;
  }

  /**
   * Extracts the parts of the book
   * 
   * @param resources
   * @return
   * @throws IOException
   */
  public List<List<List<Epubline>>> extractParts(List<Resource> resources) throws IOException {
    List<List<List<Epubline>>> parts = new ArrayList<List<List<Epubline>>>();
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

  /**
   * Builds a part with commited parameters
   * 
   * @param newResource
   * @param newSection
   * @param sections
   * @param resources
   * @return
   * @throws IOException
   */
  private List<List<Epubline>> partBuilder(Resource newResource, Element newSection,
      Elements sections, List<Resource> resources) throws IOException {
    List<List<Epubline>> partChapters = new ArrayList<List<Epubline>>();

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
      Elements nextSections = document.select(Constants.SECTION);
      for (Element sectionItem : nextSections) {
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

  /**
   * Adds the chapter to a part
   * 
   * @param partChapters
   * @param section
   * @throws IOException
   */
  private void addChapterToPart(List<List<Epubline>> partChapters, Element section)
      throws IOException {
    Elements chapterParagraphs = section.getAllElements();
    chapterParagraphs.remove(0);
    List<Epubline> chapter = getChapterLines(chapterParagraphs);
    partChapters.add(chapter);
  }
}
