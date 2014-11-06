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

public class Epub3Extractor extends AbstractEpubExtractor {

  private org.jsoup.nodes.Document document;
  private ContentBuilder contentBuilder = new ContentBuilder();
  private Elements sections;
  private ChapterPositionMaker chapterPositionMaker;
  private EmptyLinesRemover emptyLinesRemover;
  private PartsAndChaptersReviser reviser;

  public Epub3Extractor(Book book) throws IOException {
    super(book);
  }

  // Checks if ebook has parts
  private boolean existsPartInEpub3() throws IOException {
    if (!(resources == null) && !resources.isEmpty()) {
      for (Resource resourceItem : resources) {
        if (resourceItem != null) {
          document =
              Jsoup.parse(contentBuilder.getStringFromInputStream(resourceItem.getInputStream()));
          sections = document.select(Constants.SECTION);
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


  private List<List<String>> extractChapters() throws IOException {
    List<List<String>> chapters = new ArrayList<List<String>>();
    if (!(resources == null) && !resources.isEmpty()) {
      for (Resource resourceItem : resources) {
        if (resourceItem != null) {
          document =
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

  private List<List<Line>> getPartLines() throws IOException {
    emptyLinesRemover = new EmptyLinesRemover();
    List<List<Line>> partLines = new ArrayList<List<Line>>();
    List<Line> chaptersLines = new ArrayList<Line>();

    List<List<String>> currentPart = new ArrayList<List<String>>();
    currentPart = extractChapters();
    emptyLinesRemover.removeEmptyLinesPart(currentPart);


    for (List<String> chapter : currentPart) {
      for (String chapterLine : chapter) {
        chaptersLines.add(new EpubModuleLine(chapterLine));
        chaptersLines.add(new EpubModuleLine(""));
      }
    }
    partLines.add(chaptersLines);
    return partLines;
  }

  private List<List<Line>> getPartsLines() throws IOException {
    emptyLinesRemover = new EmptyLinesRemover();
    List<List<Line>> partsLines = new ArrayList<List<Line>>();
    List<List<List<String>>> currentParts = new ArrayList<List<List<String>>>();
    currentParts = extractParts();
    emptyLinesRemover.removeEmptyLinesParts(currentParts);

    for (List<List<String>> part : currentParts) {
      List<Line> chaptersLines = new ArrayList<Line>();
      for (List<String> chapter : part) {
        for (String chapterLine : chapter) {

          chaptersLines.add(new EpubModuleLine(chapterLine));
          chaptersLines.add(new EpubModuleLine(""));
        }
      }
      partsLines.add(chaptersLines);
    }
    return partsLines;
  }

  private List<String> getChapterLines(Elements chapterParagraphs) {
    List<String> chapterLines = new ArrayList<String>();
    for (Element chapterParagraph : chapterParagraphs) {
      chapterLines.add(chapterParagraph.ownText());
    }
    return chapterLines;

  }

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

  private List<List<List<String>>> extractParts() throws IOException {
    List<List<List<String>>> parts = new ArrayList<List<List<String>>>();
    if (!resources.isEmpty()) {
      for (Resource resourceItem : resources) {
        if (resourceItem != null) {
          document =
              Jsoup.parse(contentBuilder.getStringFromInputStream(resourceItem.getInputStream()));
          sections = document.select(Constants.SECTION);
          for (Element sectionItem : sections) {
            if (sectionItem.attr(Constants.EPUB_TYPE).toLowerCase().contains(Constants.EPUB3_PART)) {
              parts.add(partBuilder(resourceItem, sectionItem));
            }
          }
        }
      }
    }
    return parts;
  }

  private List<List<String>> partBuilder(Resource newResource, Element newSection)
      throws IOException {
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
      document =
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

  @Override
  public List<List<Line>> getPartList() throws IOException {
    if (existsPartInEpub3()) {

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
    
    if (existsPartInEpub3()) {
      List<List<List<String>>> currentParts = new ArrayList<List<List<String>>>();
      currentParts = extractParts();
      emptyLinesRemover.removeEmptyLinesParts(currentParts);
      List<ChapterPosition> chapterPositionsParts = new ArrayList<ChapterPosition>();
      for (List<List<String>> part : reviser.formatePartsEpub3(currentParts)) {
        chapterPositionsParts.add(chapterPositionMaker.calculateChapterPositionsEpub3(part));
      }
      return chapterPositionsParts;

    } else {
      List<List<String>> currentPart = new ArrayList<List<String>>();
      currentPart = extractChapters();
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
