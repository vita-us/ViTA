package de.unistuttgart.vis.vita.importer.epub;

import java.util.List;

import nl.siegmann.epublib.domain.Book;
import de.unistuttgart.vis.vita.importer.txt.util.ChapterPosition;

public class ChapterPositionMaker {

  private Epub2TraitsExtractor epublineTraitsExtractor;

  public ChapterPosition calculateChapterPositionsEpub2(List<List<Epubline>> newChapters,
      Book newBook, int textStartOffset) {

    epublineTraitsExtractor = new Epub2TraitsExtractor(newBook);
    int currentSize = 0;
    ChapterPosition chapterPosition = new ChapterPosition();
    for (List<Epubline> chapter : newChapters) {

      chapterPosition
          .addChapter(chapter.indexOf(epublineTraitsExtractor.getHeading(chapter)) + currentSize,
              chapter.indexOf(epublineTraitsExtractor.getTextStart(chapter))-textStartOffset + currentSize,
              chapter.indexOf(epublineTraitsExtractor.getTextEnd(chapter)) + currentSize);

      currentSize = currentSize + chapter.size();
    }
    return chapterPosition;
  }

  public ChapterPosition calculateChapterPositionsEpub3(List<List<String>> newChapters) {

    int currentSize = 0;
    ChapterPosition chapterPosition = new ChapterPosition();
    for (List<String> chapter : newChapters) {
     
       
        chapterPosition.addChapter(chapter.indexOf(chapter.get(0)) + currentSize,
            chapter.indexOf(chapter.get(2)) + currentSize,
            chapter.indexOf(chapter.get(chapter.size() - 2)) + currentSize);
        currentSize = currentSize + chapter.size();
    }
    return chapterPosition;

  }
}
