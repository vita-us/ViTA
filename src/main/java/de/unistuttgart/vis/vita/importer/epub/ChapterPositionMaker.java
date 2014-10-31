package de.unistuttgart.vis.vita.importer.epub;

import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import de.unistuttgart.vis.vita.importer.txt.util.ChapterPosition;

public class ChapterPositionMaker {

  private EpublineTraitsExtractor epublineTraitsExtractor;

  public List<ChapterPosition> calculateChapterPositions(List<List<Epubline>> newChapters,
      Book newBook) {

    epublineTraitsExtractor = new EpublineTraitsExtractor(newBook);
    List<ChapterPosition> chapterPositions = new ArrayList<ChapterPosition>();
    int currentSize = 0;
    for (List<Epubline> chapter : newChapters) {
      ChapterPosition chapterPosition = new ChapterPosition();
      chapterPosition
          .addChapter(chapter.indexOf(epublineTraitsExtractor.getHeading(chapter)) + currentSize,
              chapter.indexOf(epublineTraitsExtractor.getTextStart(chapter)) + currentSize,
              chapter.indexOf(epublineTraitsExtractor.getTextEnd(chapter)) + currentSize);
      chapterPositions.add(chapterPosition);
      currentSize = currentSize + chapter.size();

    }
    return chapterPositions;
  }

}
