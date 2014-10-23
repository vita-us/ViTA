package de.unistuttgart.vis.vita.importer.epub;

import de.unistuttgart.vis.vita.model.document.Chapter;

public class EpubChapterBuilder {

  public Chapter buildChapter(String text) {
    Chapter chapter = new Chapter();
    // todo
    // chapter.setNumber(chapterNumber);
    // chapter.setTitle(heading);
    chapter.setText(text);
    return chapter;
  }
}
