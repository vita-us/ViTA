package de.unistuttgart.vis.vita.importer.epub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Resource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Extracts various traits regarding Epub2
 * 
 *
 */
public class Epub2TraitsExtractor {
  private final List<Resource> resources;
  private final Resource tocResource;

  private ContentBuilder contentBuilder = new ContentBuilder();

  public Epub2TraitsExtractor(List<Resource> resources, Resource tocResource) {
    this.resources = resources;
    this.tocResource = tocResource;
  }

  /**
   * Extracts the lines of a chapter and transforms them into a List<Epubline>
   * @param currentElement
   * @param document
   * @param currentResource
   * @param ids
   * @return
   * @throws IOException
   */
  public List<Epubline> extractChapterEpublines(Element currentElement, Document document,
      Resource currentResource, List<String> ids) throws IOException {

    List<Epubline> chapter = new ArrayList<Epubline>();
    int start = getSubheadingPosition(currentElement, document, chapter, ids);

    // iterate through the current resource
    for (int i = document.getAllElements().indexOf(currentElement) + start; i < document
        .getAllElements().size(); i++) {

      if (!ids.contains(document.getAllElements().get(i).id())) {
        if (!document.getAllElements().get(i).text().isEmpty()
            && !document.getAllElements().get(i).attr(Constants.CLASS).matches(Constants.TOC+"|"+Constants.FOOT)
            && (document.getAllElements().get(i).tagName().equals(Constants.PARAGRAPH_TAGNAME) || document.getAllElements()
                .get(i).attr(Constants.CLASS).matches(Constants.PGMONOSPACED))) {

          chapter.add(new Epubline(Constants.TEXT, document.getAllElements().get(i).text(), ""));
        }
      } else {
        return chapter;
      }
    }

    // iterate through the remaining resources
    for (int j = resources.indexOf(currentResource) + 1; j < resources.size(); j++) {
      document =
          Jsoup.parse(contentBuilder.getStringFromInputStream(resources.get(j)
              .getInputStream()));
      for (int k = 0; k < document.getAllElements().size(); k++) {

        if (!ids.contains(document.getAllElements().get(k).id())) {
          if (!document.getAllElements().get(k).text().isEmpty()
              && !document.getAllElements().get(k).attr(Constants.CLASS).matches(Constants.TOC+"|"+Constants.FOOT)
              && (document.getAllElements().get(k).tagName().equals(Constants.PARAGRAPH_TAGNAME) || document
                  .getAllElements().get(k).attr(Constants.CLASS).matches(Constants.PGMONOSPACED))) {
            chapter.add(new Epubline(Constants.TEXT, document.getAllElements().get(k).text(), ""));
          }
        } else {
          return chapter;
        }
      }
    }
    return chapter;
  }

  /**
   * Returns the correct Epubline regarding the HEADING
   * @param chapter
   * @return
   */
  public Epubline getHeading(List<Epubline> chapter) {
    for (Epubline epubline : chapter) {
      if (epubline.getMode().equals(Constants.HEADING)) {
        return epubline;
      }
    }
    return null;
  }

  /**
   * Returns the correct Epubline regarding the TEXTSTART
   * @param chapter
   * @return
   */
  public Epubline getTextStart(List<Epubline> chapter) {
    for (Epubline epubline : chapter) {
      if (epubline.getMode().equals(Constants.TEXTSTART)) {
        return epubline;
      }
    }
    return null;
  }

  /**
   * Returns the correct Epubline regarding the TEXTEND
   * @param chapter
   * @return
   */
  public Epubline getTextEnd(List<Epubline> chapter) {
    for (Epubline epubline : chapter) {
      if (epubline.getMode().equals(Constants.TEXTEND)) {
        return epubline;
      }
    }
    return null;
  }

  /**
   * Returns the correct position of the Subheading and adds the text to the chapter
   * @param currentElement
   * @param document
   * @param chapter
   * @param ids
   * @return
   */
  private int getSubheadingPosition(Element currentElement, Document document,
      List<Epubline> chapter, List<String> ids) {
   
    int start = 1;
    if (ids.contains(document.getAllElements()
        .get(document.getAllElements().indexOf(currentElement) + 1).id())
        || document.getAllElements().get(document.getAllElements().indexOf(currentElement) + 1)
            .attr(Constants.CLASS).matches(Constants.CHAPTER_TITLE)) {

      chapter.add(new Epubline(Constants.SUBHEADING, document.getAllElements()
          .get(document.getAllElements().indexOf(currentElement) + 1).text(), document
          .getAllElements().get(document.getAllElements().indexOf(currentElement) + 1).id()));
      start = 2;
    }
    return start;
  }

  /**
   * Returns a List<List<List<Epubline>>> which contains parts of a book
   * @param chapters
   * @return
   * @throws IOException
   */
  public List<List<List<Epubline>>> getPartsEpublines(List<List<Epubline>> chapters)
      throws IOException {
    
    List<List<String>> partsWithChaptersIds = new ArrayList<List<String>>();
    Epub2IdsAndTitlesExtractor epub2IdsExtracor =
        new Epub2IdsAndTitlesExtractor(resources, tocResource);
    partsWithChaptersIds = epub2IdsExtracor.getPartsChaptersIds();
    
    List<List<List<Epubline>>> parts = new ArrayList<List<List<Epubline>>>();

    for (List<String> part : partsWithChaptersIds) {
      List<List<Epubline>> partChapters = new ArrayList<List<Epubline>>();
      for (int i = 0; i < part.size(); i++) {
        for (List<Epubline> chapter : chapters) {
          if (part.get(i).matches(chapter.get(0).getId())) {
            partChapters.add(chapter);
          }

        }
      }
      parts.add(partChapters);
    }

    return parts;
  }

}
