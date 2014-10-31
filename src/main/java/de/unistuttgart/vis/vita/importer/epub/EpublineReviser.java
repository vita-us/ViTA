package de.unistuttgart.vis.vita.importer.epub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EpublineReviser {


  public List<List<Epubline>> formateChapters(List<List<Epubline>> chapters) {

    List<List<Epubline>> formatedChapters = new ArrayList<List<Epubline>>();

    for (List<Epubline> chapter : chapters) {
      List<Epubline> newChapter = new ArrayList<Epubline>();
      for (Epubline epubline : chapter) {
        newChapter.add(epubline);
        newChapter.add(new Epubline(Constants.TEXT, "", ""));
      }
      formatedChapters.add(newChapter);
    }
    return formatedChapters;
  }

  public List<List<List<Epubline>>> formateParts(List<List<List<Epubline>>> parts)
      throws IOException {

    List<List<List<Epubline>>> formatedParts = new ArrayList<List<List<Epubline>>>();

    for (List<List<Epubline>> part : parts) {
      List<List<Epubline>> newPart = new ArrayList<List<Epubline>>();
      for (List<Epubline> chapter : part) {
        List<Epubline> newChapter = new ArrayList<Epubline>();
        for (Epubline epubline : chapter) {
          newChapter.add(epubline);
          newChapter.add(new Epubline(Constants.TEXT, "", ""));
        }
        newPart.add(newChapter);
      }
      formatedParts.add(newPart);
    }
    return formatedParts;
  }

  public void annotateTextStartAndEndOfEpublines(List<Epubline> epublines) {
    Epubline currentEpubline = new Epubline("", "", "");
    int position = 0;
    if (epublines.size() > 2) {
      for (Epubline epubline : epublines) {
        if (epubline.getMode().matches(Constants.TEXT)) {
          position = epublines.indexOf(epubline);
          currentEpubline = epubline;
          currentEpubline.setMode(Constants.TEXTSTART);
          break;
        }
      }
      epublines.set(position, currentEpubline);
      epublines.get(epublines.size() - 1).setMode(Constants.TEXTEND);
    }
  }
}
