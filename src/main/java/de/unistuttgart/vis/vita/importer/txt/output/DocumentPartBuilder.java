package de.unistuttgart.vis.vita.importer.txt.output;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.unistuttgart.vis.vita.importer.txt.util.ChapterPosition;
import de.unistuttgart.vis.vita.importer.txt.util.Line;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

/**
 * Implements Callable - returning a DocumentPart. <br>
 * <br>
 * The DocumentPartBuilder is the link between Chapter Analyzer and the DocumentPart Objects used by
 * all other components of ViTA. It transforms the result of the Chapter Analyzer into a
 * DocumentPart.<br>
 * <br>
 * The structure of given List-parameters can be changed by this class.
 */
public class DocumentPartBuilder implements Callable<DocumentPart> {
  private List<Future<Chapter>> futureChapters = new ArrayList<Future<Chapter>>();
  private List<Line> lines;
  private ChapterPosition chapterPositions;
  private int partNumber;

  /**
   * Instantiates a new DocumentPartBuilder, the DocumentPart will be build when calling the method
   * 'call()'. It is necessary to determine the Document the Chapters of the DocumentPart belong to.
   * Please note that the Document of a Chapter can not be changed later!
   * 
   * @param lines ArrayList<Line> - Consisting of all headings and texts for the Chapters in the
   *        correct order.
   * @param chapterPositions ChapterPosition - Contains the information of the position of heading
   *        and text areas of the Chapters in lines.
   */
  public DocumentPartBuilder(List<Line> lines, ChapterPosition chapterPositions) {
    this(lines, chapterPositions, 1);
  }

  /**
   * Instantiates a new DocumentPartBuilder, the DocumentPart will be build when calling the method
   * 'call()'. It is necessary to determine the Document the Chapters of the DocumentPart belong to.
   * Please note that the Document of a Chapter can not be changed later!
   * 
   * @param lines Consisting of all headings and texts for the Chapters in the correct order.
   * @param chapterPositions ChapterPosition - Contains the information of the position of heading
   *        and text areas of the Chapters in lines.
   * @param partNumber int - Number of the DocumentPart of the Document.
   */
  public DocumentPartBuilder(List<Line> lines, ChapterPosition chapterPositions, int partNumber) {
    this.lines = lines;
    this.chapterPositions = chapterPositions;
    this.partNumber = partNumber;
  }

  /**
   * Starts the Threads for the construction of all Chapters and adds the Objects to futureChapters.
   */
  private void startChapterComputation() {
    ExecutorService executor = Executors.newCachedThreadPool();
    for (int chapterNumber = 1; chapterNumber <= chapterPositions.size(); chapterNumber++) {
      List<Line> heading = buildHeadingList(chapterNumber);
      List<Line> text = buildTextList(chapterNumber);
      Callable<Chapter> chapterBuilder = new ChapterBuilder(heading, text, chapterNumber);
      Future<Chapter> futureChapter = executor.submit(chapterBuilder);
      futureChapters.add(futureChapter);
    }
  }

  /**
   * Builds a real sub-list for the heading of exactly one Chapter.
   * 
   * @param chapterNumber int - The number of the Chapter in chapterPositions.
   * @return ArrayList<Line> - The list contains all lines of the Chapter's heading.
   */
  private List<Line> buildHeadingList(int chapterNumber) {
    int startOfHeading = chapterPositions.getStartOfHeading(chapterNumber);
    int startOfText = chapterPositions.getStartOfText(chapterNumber);

    List<Line> heading;
    if (chapterPositions.hasHeading(chapterNumber)) {
      heading = new ArrayList<Line>(lines.subList(startOfHeading, startOfText));
    } else {
      heading = new ArrayList<Line>();
      // set default value
      heading.add(new Line("Chapter " + chapterNumber, false));
    }
    return heading;
  }

  /**
   * Builds a real sub-list for the text of exactly one Chapter.
   * 
   * @param chapterNumber int - The number of the Chapter in chapterPositions.
   * @return The list contains all lines of the Chapter's text.
   */
  private List<Line> buildTextList(int chapterNumber) {
    int startOfText = chapterPositions.getStartOfText(chapterNumber);
    int endOfText = chapterPositions.getEndOfText(chapterNumber);
    return new ArrayList<Line>(lines.subList(startOfText, endOfText + 1));
  }

  /**
   * For all started Threads the results of the computation will be returned in a List of Chapters.
   * 
   * @return List of Chapter - The created chapters.
   */
  private List<Chapter> buildChapterList() {
    List<Chapter> chapters = new ArrayList<Chapter>();
    for (Future<Chapter> futureChapter : futureChapters) {
      try {
        chapters.add(futureChapter.get());
      } catch (InterruptedException | ExecutionException e) {
        // try next one
      }
    }
    return chapters;
  }

  /**
   * Builds the DocumentPart and sets all known attributes.
   * 
   * @param partNumber int - The number of this part in the Document.
   * @param chapters List of Chapter - All Chapters of the DocumentPart.
   * @return DocumentPart - The constructed DocumentPart.
   */
  private DocumentPart buildDocumentPart(int partNumber, List<Chapter> chapters) {
    DocumentPart documentPart = new DocumentPart();
    documentPart.getChapters().addAll(chapters);
    documentPart.setNumber(partNumber);
    return documentPart;
  }

  @Override
  public DocumentPart call() {
    startChapterComputation();
    List<Chapter> chapters = buildChapterList();
    return buildDocumentPart(partNumber, chapters);
  }
}
