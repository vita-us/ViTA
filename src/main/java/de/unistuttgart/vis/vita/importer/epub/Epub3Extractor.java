package de.unistuttgart.vis.vita.importer.epub;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.unistuttgart.vis.vita.importer.txt.util.ChapterPosition;
import de.unistuttgart.vis.vita.importer.txt.util.Line;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

/**
 * Returns the output of Epub3 in terms of the abstract methods
 * 
 *
 */
public class Epub3Extractor extends AbstractEpubExtractor {

  private org.jsoup.nodes.Document document;
  private ContentBuilder contentBuilder = new ContentBuilder();
  private Elements sections;
  private ChapterPositionMaker chapterPositionMaker;
  private EmptyLinesRemover emptyLinesRemover;
  private PartsAndChaptersReviser reviser;
  private Epub3TraitsExtractor epub3TraitsExtractor = new Epub3TraitsExtractor();

  /**
   * The commited book will be used in the methods below.
   * 
   * @param book
   * @throws IOException
   */
  public Epub3Extractor(Book book) throws IOException {
    super(book);
  }

  /**
   * Empty Strings will be removed in the String chapters and transforms them into a List<List<Line>>
   * 
   * @return
   */
  private List<List<Line>> getPartLines() throws IOException {
    emptyLinesRemover = new EmptyLinesRemover();
    List<List<Line>> partLines = new ArrayList<List<Line>>();
    List<Line> chaptersLines = new ArrayList<Line>();

    List<List<Epubline>> currentPart = new ArrayList<List<Epubline>>();
    currentPart = epub3TraitsExtractor.extractChapters(resources);
    emptyLinesRemover.removeEmptyLinesPart(currentPart);

    for (List<Epubline> chapter : currentPart) {
      for (Epubline chapterLine : chapter) {
        chaptersLines.add(new EpubModuleLine(chapterLine.getEpubline()));
        chaptersLines.add(new EpubModuleLine(""));
      }
    }
    partLines.add(chaptersLines);
    return partLines;
  }

  /**
   * Empty Strings will be removed in the String parts and transforms them into a List<List<Line>>
   * 
   * @return
   */
  private List<List<Line>> getPartsLines() throws IOException {
    emptyLinesRemover = new EmptyLinesRemover();
    List<List<Line>> partsLines = new ArrayList<List<Line>>();
    List<List<List<Epubline>>> currentParts = new ArrayList<List<List<Epubline>>>();
    currentParts = epub3TraitsExtractor.extractParts(resources);
    emptyLinesRemover.removeEmptyLinesParts(currentParts);

    for (List<List<Epubline>> part : currentParts) {
      List<Line> chaptersLines = new ArrayList<Line>();
      for (List<Epubline> chapter : part) {
        for (Epubline chapterLine : chapter) {

          chaptersLines.add(new EpubModuleLine(chapterLine.getEpubline()));
          chaptersLines.add(new EpubModuleLine(""));
        }
      }
      partsLines.add(chaptersLines);
    }
    return partsLines;
  }

  // TODO
  public List<String> test() throws IOException {
    List<String> chapter = new ArrayList<String>();
    String span = "";
    document =
        Jsoup.parse(contentBuilder.getStringFromInputStream(resources.get(6).getInputStream()));
    sections = document.select(Constants.SECTION);
    Elements elements = sections.get(0).getAllElements();
    for (int i = 1; i < elements.size(); i++) {
      if (!elements.get(i).tagName().matches("span")) {
        if (!elements.get(i).getAllElements().isEmpty()) {
          Elements innerElements = elements.get(i).getAllElements();

          if (existsSpan(innerElements)) {
            if (elements.get(i).ownText().isEmpty()) {
              for (Element innerElement : innerElements) {
                if (innerElement.tagName().matches("span")) {
                  span += innerElement.ownText() + " ";
                }
              }
              chapter.add(span);
              span = "";
            }else{
              chapter.add(elements.get(i).text());
            }
          } else {
            chapter.add(elements.get(i).ownText());
          }
        } else {
          chapter.add(elements.get(i).ownText());
        }
      }
    }
    return chapter;
  }

  public boolean existsSpan(Elements innerElements) {
    for (Element innerElement : innerElements) {
      if (innerElement.tagName().matches("span")) {
        return true;
      }
    }
    return false;
  }

  @Override
  public List<List<Line>> getPartList() throws IOException {
    if (epub3TraitsExtractor.existsPartInEpub3(resources)) {

      return getPartsLines();
    } else {
      return getPartLines();
    }
  }

  @Override
  public List<ChapterPosition> getChapterPositionList() throws IOException {
    chapterPositionMaker = new ChapterPositionMaker();
    emptyLinesRemover = new EmptyLinesRemover();
    reviser = new PartsAndChaptersReviser();
    
    if (epub3TraitsExtractor.existsPartInEpub3(resources)) {
      List<List<List<Epubline>>> currentParts = new ArrayList<List<List<Epubline>>>();
      currentParts = epub3TraitsExtractor.extractParts(resources);
      emptyLinesRemover.removeEmptyLinesParts(currentParts);
      List<ChapterPosition> chapterPositionsParts = new ArrayList<ChapterPosition>();
      for (List<List<Epubline>> part : reviser.formatePartsEpub3(currentParts)) {
        chapterPositionsParts.add(chapterPositionMaker.calculateChapterPositionsEpub3(part));
      }
      return chapterPositionsParts;

    } else {
      List<List<Epubline>> currentPart = new ArrayList<List<Epubline>>();
      currentPart = epub3TraitsExtractor.extractChapters(resources);
      emptyLinesRemover.removeEmptyLinesPart(currentPart);
      List<ChapterPosition> chapterPositionsPart = new ArrayList<ChapterPosition>();
      chapterPositionsPart.add(chapterPositionMaker
          .calculateChapterPositionsEpub3(reviser.formatePartEpub3(currentPart)));
      return chapterPositionsPart;
    }
  }

  @Override
  public List<String> getTitleList() throws IOException {
    List<String> titleList = new ArrayList<String>();

    if (!resources.isEmpty()) {
      for (Resource resourceItem : resources) {
        if (resourceItem != null) {
          document =
              Jsoup.parse(contentBuilder.getStringFromInputStream(resourceItem.getInputStream()));
          sections = document.select(Constants.SECTION);
          for (Element sectionItem : sections) {
            if (sectionItem.attr(Constants.EPUB_TYPE).toLowerCase().contains(Constants.EPUB3_PART)) {
              titleList.add(sectionItem.text());
            }
          }
        }
      }
    }
    return titleList;
  }
}
