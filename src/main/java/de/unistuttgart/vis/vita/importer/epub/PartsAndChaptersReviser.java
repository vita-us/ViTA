package de.unistuttgart.vis.vita.importer.epub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Revises the parts and chapters in terms of formatting, annotating the parts and chapters
 * 
 *
 */
public class PartsAndChaptersReviser {

  /**
   * In the commited part after every line a empty line will be added
   * @param part
   * @return
   */
  public List<List<Epubline>> formatePartEpub2(List<List<Epubline>> part) {

    List<List<Epubline>> formatedPart = new ArrayList<List<Epubline>>();
    for (List<Epubline> chapter : part) {
      List<Epubline> newChapter = new ArrayList<Epubline>();
      for (Epubline epubline : chapter) {
        newChapter.add(epubline);
        newChapter.add(new Epubline(Constants.TEXT, "", ""));
      }
      formatedPart.add(newChapter);
    }
    return formatedPart;
  }

  /**
   * In the commited parts after every line a empty line will be added
   * @param parts
   * @return
   */
  public List<List<List<Epubline>>> formatePartsEpub2(List<List<List<Epubline>>> parts)
      throws IOException {

    List<List<List<Epubline>>> formatedParts = new ArrayList<List<List<Epubline>>>();

    for (List<List<Epubline>> part : parts) {
      formatedParts.add(formatePartEpub2(part));
    }
    return formatedParts;
  }

  /**
   * Annotates the correct epublines with TEXTSTART and TEXTEND
   * @param epublines
   */
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

  /**
   * In the commited part after every line a empty line will be added
   * @param currentPart
   * @return
   */
  public List<List<String>> formatePartEpub3(List<List<String>> currentPart) {
    List<List<String>> formatedPart = new ArrayList<List<String>>();
    for (List<String> chapter : currentPart) {
      List<String> newChapter = new ArrayList<String>();
      for (String line : chapter) {
        newChapter.add(line);
        newChapter.add("");
      }
      formatedPart.add(newChapter);
    }
    return formatedPart;
  }

  /**
   * In the commited parts after every line a empty line will be added
   * @param currentParts
   * @return
   */
  public List<List<List<String>>> formatePartsEpub3(List<List<List<String>>> currentParts) {
    List<List<List<String>>> formatedParts = new ArrayList<List<List<String>>>();
    for (List<List<String>> part : currentParts) {
      formatedParts.add(formatePartEpub3(part));
    }
    return formatedParts;
  }
}
