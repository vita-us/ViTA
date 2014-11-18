package de.unistuttgart.vis.vita.importer.epub.extractors;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import de.unistuttgart.vis.vita.importer.epub.util.EpubModuleLine;
import de.unistuttgart.vis.vita.importer.util.ChapterPosition;
import de.unistuttgart.vis.vita.importer.util.Line;

/**
 * Returns the output of Epub2 in terms of the abstract methods
 * 
 *
 */
public class Epub2Extractor extends AbstractEpubExtractor {

  private Document document;
  private ContentBuilder contentBuilder = new ContentBuilder();
  private List<List<Epubline>> chapters = new ArrayList<List<Epubline>>();
  private PartsAndChaptersReviser reviser = new PartsAndChaptersReviser();
  private ChapterPositionMaker chapterPositionMaker = new ChapterPositionMaker();
  private Epub2TraitsExtractor epublineTraitsExtractor;
  private Epub2IdsAndTitlesExtractor epub2IdsExtractor;

  /**
   * The commited book will be used in the methods below. The instances of Epub2TraitsExtractor and
   * Epub2IdsAndTitlesExtractor will be created. The method extractChaptersEpub2 will be called.
   * 
   * @param book
   * @throws IOException
   */
  public Epub2Extractor(Book book) throws IOException {
    super(book);
    epublineTraitsExtractor = new Epub2TraitsExtractor(resources, tocResource);
    epub2IdsExtractor = new Epub2IdsAndTitlesExtractor(resources, tocResource);
    extractChaptersEpub2();
  }

  /**
   * Extracts the chapters of the ebook and transforms them into a List<List<Epubline>>
   * 
   * @throws IOException
   */
  private void extractChaptersEpub2() throws IOException {
    List<String> tocIds = epub2IdsExtractor.getTocIds();

    if (!tocIds.isEmpty()) {
      for (Resource resource : resources) {
        document = Jsoup.parse(contentBuilder.getStringFromInputStream(resource.getInputStream()));

        addLinesToChapter(tocIds, resource);
      }
      removeEmptyChapters(chapters);
    }
  }

  /**
   * Adds the lines of chapter to chapters list
   * 
   * @param tocIds
   * @param resource
   * @throws IOException
   */
  private void addLinesToChapter(List<String> tocIds, Resource resource) throws IOException {
    for (String id : tocIds) {
      Element currentElement = document.getElementById(id);

      if (currentElement != null && !epubLineExistent(currentElement)) {
        List<Epubline> epublines = new ArrayList<Epubline>();
        epublines.addAll(epublineTraitsExtractor.extractChapterEpublines(currentElement, document,
            resource, tocIds));
        epublines.add(0,
            new Epubline(Constants.HEADING, currentElement.text(), currentElement.id()));
        reviser.annotateTextStartAndEndOfEpublines(epublines);
        chapters.add(epublines);
      }
    }
  }

  /**
   * Checks if the currentElement is already in the chapters
   * 
   * @param currentElement
   * @return
   */
  private boolean epubLineExistent(Element currentElement) {
    for (List<Epubline> chapter : chapters) {
      for (Epubline epubline : chapter) {
        if (epubline.getId().matches(currentElement.id())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Removes empty List<Epubline> or List<Epubline> size <= 2
   * 
   * @param chapters
   */
  private void removeEmptyChapters(List<List<Epubline>> chapters) {
    List<List<Epubline>> chaptersToRemove = new ArrayList<List<Epubline>>();
    for (List<Epubline> chapter : chapters) {
      if (chapter.size() <= 2) {
        chaptersToRemove.add(chapter);
      }
    }
    chapters.removeAll(chaptersToRemove);
    chaptersToRemove.clear();
  }

  /**
   * Transforms the formated chapters into a List<List<Line>>
   * 
   * @return
   */
  private List<List<Line>> getPartLines() {
    List<List<Line>> partLines = new ArrayList<List<Line>>();
    List<Line> chaptersLines = new ArrayList<Line>();

    for (List<Epubline> chapter : reviser.formatePartEpub2(chapters)) {
      for (Epubline epubline : chapter) {
        chaptersLines.add(new EpubModuleLine(epubline.getEpubline()));
      }
    }
    partLines.add(chaptersLines);
    return partLines;
  }


  /**
   * Transforms the formated chapters into parts and then transforms the parts into several
   * List<List<Line>>
   * 
   * @return
   * @throws IOException
   */
  private List<List<Line>> getPartsLines() throws IOException {
    List<List<Line>> partsLines = new ArrayList<List<Line>>();

    for (List<List<Epubline>> part : reviser.formatePartsEpub2(epublineTraitsExtractor
        .getPartsEpublines(chapters))) {
      List<Line> chaptersLines = new ArrayList<Line>();
      for (List<Epubline> chapter : part) {
        for (Epubline epubline : chapter) {
          chaptersLines.add(new EpubModuleLine(epubline.getEpubline()));
        }
      }
      partsLines.add(chaptersLines);
    }
    return partsLines;
  }

  /**
   * Adds each part to the List<List<List<Epubline>>>
   * 
   * @param parts
   * @throws IOException
   */
  private void addEpublinesToList(List<List<List<Epubline>>> parts) throws IOException {
    for (List<List<Epubline>> part : reviser.formatePartsEpub2(epublineTraitsExtractor
        .getPartsEpublines(chapters))) {
      parts.add(part);
    }
  }

  @Override
  public List<List<Line>> getPartList() throws IOException {

    if (epub2IdsExtractor.existsPart()) {
      return getPartsLines();
    } else {
      return getPartLines();
    }

  }

  @Override
  public List<ChapterPosition> getChapterPositionList() throws IOException {
    if (epub2IdsExtractor.existsPart()) {

      List<ChapterPosition> chapterPositionsParts = new ArrayList<ChapterPosition>();
      List<List<List<Epubline>>> parts = new ArrayList<List<List<Epubline>>>();
      addEpublinesToList(parts);
      for (List<List<Epubline>> part : parts) {
        chapterPositionsParts.add(chapterPositionMaker.calculateChapterPositionsEpub2(part,
            resources, tocResource));
      }

      return chapterPositionsParts;
    } else {
      List<ChapterPosition> chapterPositionsPart = new ArrayList<ChapterPosition>();
      List<List<Epubline>> part = reviser.formatePartEpub2(chapters);
      chapterPositionsPart.add(chapterPositionMaker.calculateChapterPositionsEpub2(part, resources,
          tocResource));

      return chapterPositionsPart;
    }
  }

  @Override
  public List<String> getTitleList() throws IOException {
    List<String> titleList = new ArrayList<String>();

    if (epub2IdsExtractor.existsPart()) {
      titleList = epub2IdsExtractor.getPartsTitles();

      return titleList;
    } else {
      return titleList;
    }

  }
}
