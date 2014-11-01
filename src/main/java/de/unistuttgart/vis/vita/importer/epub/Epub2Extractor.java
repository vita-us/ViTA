package de.unistuttgart.vis.vita.importer.epub;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import de.unistuttgart.vis.vita.importer.txt.util.ChapterPosition;
import de.unistuttgart.vis.vita.importer.txt.util.Line;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

public class Epub2Extractor extends AbstractEpubExtractor {

  private Document document;
  private ContentBuilder contentBuilder = new ContentBuilder();
  private Book book = new Book();
  private List<List<Epubline>> chapters = new ArrayList<List<Epubline>>();
  private EpublineReviser epublineReviser = new EpublineReviser();
  private ChapterPositionMaker chapterPositionMaker = new ChapterPositionMaker();
  private EpublineTraitsExtractor epublineTraitsExtractor;
  private Epub2IdsExtractor epub2IdsExtractor;

  public Epub2Extractor(Book book) throws IOException {
    super(book);
    this.book = book;
    epublineTraitsExtractor = new EpublineTraitsExtractor(book);
    epub2IdsExtractor = new Epub2IdsExtractor(book);
    extractChaptersEpub2();
  }

  private void extractChaptersEpub2() throws IOException {
    List<String> tocIds = new ArrayList<String>();
    tocIds = epub2IdsExtractor.getTocIds();

    if (!tocIds.isEmpty()) {
      for (Resource resource : book.getContents()) {
        document = Jsoup.parse(contentBuilder.getStringFromInputStream(resource.getInputStream()));

        for (String id : tocIds) {
          Element currentElement = document.getElementById(id);
          if (currentElement != null) {
            List<Epubline> epublines = new ArrayList<Epubline>();

            if (!epubLineExistent(currentElement)) {
              epublines.addAll(epublineTraitsExtractor.extractChapterEpublines(currentElement,
                  document, resource, tocIds));
              epublines.add(0, new Epubline(Constants.HEADING, currentElement.text(),
                  currentElement.id()));
              epublineReviser.annotateTextStartAndEndOfEpublines(epublines);
              chapters.add(epublines);
            }
          }
        }
      }

      removeEmptyChapters(chapters);
    }
  }

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

  private List<List<Line>> getPartLines() {
    List<List<Line>> partLines = new ArrayList<List<Line>>();
    List<Line> chaptersLines = new ArrayList<Line>();

    for (List<Epubline> chapter : epublineReviser.formateChapters(chapters)) {
      for (Epubline epubline : chapter) {
        chaptersLines.add(new EpubModuleLine(epubline.getEpubline()));
      }
    }
    partLines.add(chaptersLines);
    return partLines;
  }


  private List<List<Line>> getPartsLines() throws IOException {
    List<List<Line>> partsLines = new ArrayList<List<Line>>();

    for (List<List<Epubline>> part : epublineReviser.formateParts(epublineTraitsExtractor
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

  private void addEpublinesToList(List<List<Epubline>> chaptersWithParts) throws IOException {
    for (List<List<Epubline>> part : epublineReviser.formateParts(epublineTraitsExtractor
        .getPartsEpublines(chapters))) {
      for (List<Epubline> chapter : part) {
        chaptersWithParts.add(chapter);
      }
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

      List<List<Epubline>> chaptersWithParts = new ArrayList<List<Epubline>>();
      addEpublinesToList(chaptersWithParts);

      return chapterPositionMaker.calculateChapterPositions(chaptersWithParts, book);
    } else {

      return chapterPositionMaker.calculateChapterPositions(chapters, book);
    }
  }

  @Override
  public List<String> getTitleList() throws IOException {

    if (epub2IdsExtractor.existsPart()) {
      return epub2IdsExtractor.getPartsTitles();
    }
    return null;

  }
}
