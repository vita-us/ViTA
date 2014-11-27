package de.unistuttgart.vis.vita.importer.output;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.unistuttgart.vis.vita.importer.util.ChapterPosition;
import de.unistuttgart.vis.vita.importer.util.Line;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

/**
 * Implements Callable - returning a List of DocumentParts. <br>
 * <br>
 * The BookBuilder is the link between Epub Extractor and the DocumentPart Objects used by all
 * other components of ViTA. It transforms the result of the Extractor into a List of
 * DocumentPart.<br>
 * <br>
 * The structure of the given List-parameters can be changed by this class.
 */
public class BookBuilder extends AbstractBuilder implements Callable<List<DocumentPart>> {
  private static final Logger LOG = Logger.getLogger("Exception");
  private List<Future<DocumentPart>> futureDocumentParts = new ArrayList<>();
  private List<List<Line>> partLines;
  private List<ChapterPosition> chapterPositions;
  private List<String> partTitles;
  private int size;

  /**
   * Implements Callable - returning a List of DocumentParts. <br>
   * <br>
   * The BookBuilder is the link between Epub Extractor and the DocumentPart Objects used by all
   * other components of ViTA. It transforms the result of the Extractor into a List of
   * DocumentPart.<br>
   * <br>
   * The structure of the given List-parameters can be changed by this class.
   * 
   * @param partLines All lines of the book divided in parts.
   * @param chapterPositions ChapterPositions for all the parts in partLines..
   */
  public BookBuilder(List<List<Line>> partLines, List<ChapterPosition> chapterPositions){
    this(partLines, chapterPositions, null);
  }

  /**
   * Implements Callable - returning a List of DocumentParts. <br>
   * <br>
   * The BookBuilder is the link between Epub Extractor and the DocumentPart Objects used by all
   * other components of ViTA. It transforms the result of the Extractor into a List of
   * DocumentPart.<br>
   * <br>
   * The structure of the given List-parameters can be changed by this class.
   * 
   * @param partLines All lines of the book divided in parts.
   * @param chapterPositions ChapterPositions for all the parts in partLines..
   * @param partTitles The titles of a part. N-th element belongs to the n-th part.
   */
  public BookBuilder(List<List<Line>> partLines, List<ChapterPosition> chapterPositions,
      List<String> partTitles) {
    this.partLines = partLines;
    this.chapterPositions = chapterPositions;
    this.partTitles = partTitles;

    if (this.partLines == null) {
      this.partLines = new ArrayList<List<Line>>();
    }
    if (this.chapterPositions == null) {
      this.chapterPositions = new ArrayList<ChapterPosition>();
    }

    // Use empty titles when the data has wrong size
    if (this.partTitles == null || this.partLines.size() != this.partTitles.size()) {
      this.partTitles = new ArrayList<String>();
      for (int i = 0; i < this.partLines.size(); i++) {
        this.partTitles.add("");
      }
    }

    // check input data
    if (this.partLines.size() != this.chapterPositions.size()) {
      throw new IllegalArgumentException("Input of different size " + this.partLines.size() + " and "
          + this.chapterPositions.size() + "is not allowed");
    }
    size = this.partLines.size();
  }

  /**
   * Starts the Threads for the construction of all Parts and adds the Objects to futureParts.
   */
  private void startPartComputation() {
    ExecutorService executor = Executors.newCachedThreadPool();
    for (int partIndex = 0; partIndex < size; partIndex++) {
      List<Line> currentPartText = this.partLines.get(partIndex);
      ChapterPosition currentPartChapterPosition = this.chapterPositions.get(partIndex);
      String currentPartTitle = this.partTitles.get(partIndex);

      Callable<DocumentPart> partBuilder =
          new DocumentPartBuilder(currentPartText, currentPartChapterPosition, partIndex + 1,
              currentPartTitle);
      Future<DocumentPart> futurePart = executor.submit(partBuilder);
      this.futureDocumentParts.add(futurePart);
    }
  }

  /**
   * Build the list of parts out of the future objects.
   * 
   * @return The list contains all DocumentParts.
   */
  private List<DocumentPart> buildChapterList() {
    List<DocumentPart> parts = new ArrayList<>();
    for (Future<DocumentPart> futurePart : this.futureDocumentParts) {
      try {
        parts.add(futurePart.get());
      } catch (InterruptedException | ExecutionException e) {
        // log and try next one
        LOG.log(Level.SEVERE, "Failed getting a part of the book", e);
      }
    }
    return parts;
  }


  @Override
  public List<DocumentPart> call() {
    startPartComputation();
    List<DocumentPart> parts = buildChapterList();
    BookAttributeBuilder attributeBuilder = new BookAttributeBuilder(parts);
    attributeBuilder.buildAttributes();
    return parts;
  }

}
