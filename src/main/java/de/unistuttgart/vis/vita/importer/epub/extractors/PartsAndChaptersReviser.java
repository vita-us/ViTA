package de.unistuttgart.vis.vita.importer.epub.extractors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

/**
 * Revises the parts and chapters in terms of formatting, annotating the parts and chapters
 * 
 *
 */
public class PartsAndChaptersReviser {

  /**
   * In the commited part after every line a empty line will be added
   * 
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
   * 
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
   * 
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
   * 
   * @param currentPart
   * @return
   */
  public List<List<Epubline>> formatePartEpub3(List<List<Epubline>> currentPart) {
    List<List<Epubline>> formatedPart = new ArrayList<List<Epubline>>();
    for (List<Epubline> chapter : currentPart) {
      List<Epubline> newChapter = new ArrayList<Epubline>();
      for (Epubline line : chapter) {
        newChapter.add(line);
        newChapter.add(new Epubline("", "", ""));
      }
      formatedPart.add(newChapter);
    }
    return formatedPart;
  }

  /**
   * In the commited parts after every line a empty line will be added
   * 
   * @param currentParts
   * @return
   */
  public List<List<List<Epubline>>> formatePartsEpub3(List<List<List<Epubline>>> currentParts) {
    List<List<List<Epubline>>> formatedParts = new ArrayList<List<List<Epubline>>>();
    for (List<List<Epubline>> part : currentParts) {
      formatedParts.add(formatePartEpub3(part));
    }
    return formatedParts;
  }

  /**
   * Checks if the currentElement exists in the editedElements
   * 
   * @param editedElements
   * @param currentElement
   * @return
   */
  public boolean elementEdited(List<Element> editedElements, Element currentElement) {
    for (Element editedElement : editedElements) {
      if (editedElement.equals(currentElement)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Adds the text of a div respectively the text of the intricate divs to the chapter
   * 
   * @param chapter
   * @param chapterElement
   * @param editedElements
   * @param mode
   */
  public void addDivTexts(List<Epubline> chapter, Element chapterElement,
      List<Element> editedElements, String mode) {
    if (chapterElement.ownText().isEmpty() && allElementsNotSpans(chapterElement)) { 
      if (!chapterElement.getAllElements().isEmpty()) {  
        Elements innerElements = chapterElement.getAllElements();
        innerElements.remove(0);
        addInnerElementText(chapter, editedElements, mode, innerElements);
      }
    } else {
      if (!chapterElement.getAllElements().isEmpty()) {
        Elements innerElements = chapterElement.getAllElements();
        fillEditedElements(editedElements, innerElements);
        addEpubline(chapter, chapterElement, mode);
      }
    }
  }


  /**
   * Adds the text of a element(e.g. paragraph) to the chapter
   * 
   * @param chapter
   * @param chapterElement
   * @param elementExists
   * @param mode
   */
  public void addText(List<Epubline> chapter, Element chapterElement, boolean elementExists,
      String mode) {
    if (elementExists) {
      // No br analysis with spans
      chapter.add(new Epubline(mode, chapterElement.text(), ""));
    } else {
      addEpubline(chapter, chapterElement, mode);
    }
  }

  /**
   * Checks if a span element exists in the elements of the current element
   * 
   * @param currentElement
   * @return
   */
  public boolean existsSpan(Element currentElement) {
    if (!currentElement.getAllElements().isEmpty()) {
      Elements innerElements = currentElement.getAllElements();
      for (Element innerElement : innerElements) {
        if (innerElement.tagName().matches(Constants.SPAN)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Checks if a div element exists in the elements of the current element
   * 
   * @param currentElement
   * @return
   */
  public boolean existsDiv(Element currentElement) {
    if (!currentElement.getAllElements().isEmpty()) {
      Elements innerElements = currentElement.getAllElements();
      for (Element innerElement : innerElements) {
        if (innerElement.tagName().matches(Constants.DIV)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Checks if all elements are span elements
   * 
   * @param currentElement
   * @return
   */
  public boolean allElementsNotSpans(Element currentElement) {
    if (!currentElement.getAllElements().isEmpty()) {
      
      Elements innerElements = currentElement.getAllElements();
      if (innerElements.get(0).tagName().matches(Constants.DIV)) {
        innerElements.remove(0);
      }
      for (Element innerElement : innerElements) {
        if (!innerElement.tagName().matches(Constants.SPAN)) {
          return true;
        }
      }
    }
    return false;
  }

  private void fillEditedElements(List<Element> editedElements, Elements innerElements) {
    for (Element innerElement : innerElements) {
      editedElements.add(innerElement);
    }
  }

  private void addInnerElementText(List<Epubline> chapter, List<Element> editedElements,
      String mode, Elements innerElements) {
    for (Element innerElement : innerElements) {
      if (!elementEdited(editedElements, innerElement)) {
        if (!innerElement.tagName().matches(Constants.SPAN)
            && !innerElement.tagName().matches(Constants.DIV)) {

          addText(chapter, innerElement, existsSpan(innerElement), mode);
        } else if (innerElement.tagName().matches(Constants.DIV)) {
          addDivTexts(chapter, innerElement, editedElements, mode);
        }
        editedElements.add(innerElement);
      }
    }
  }


  /**
   * Creates new Epublines and detects HTML-Linebreaks in the text.
   * 
   * @param chapter
   * @param chapterElement
   * @param mode
   */
  private void addEpubline(List<Epubline> chapter, Element chapterElement, String mode) {
    String writeNext = "";
    List<TextNode> textNodes = chapterElement.textNodes();
    int textIndex = 0;
    for (Node node : chapterElement.childNodes()) {
      if ("#text".equals(node.nodeName().trim())) {
        // text node -> add test
        writeNext = writeNext.concat(textNodes.get(textIndex).text());
        textIndex++;
      } else if ("br".equals(node.nodeName().trim())) {
        // break -> make a new line
        chapter.add(new Epubline(mode, writeNext, ""));
        writeNext = "";
      }
    }
    if (!"".equals(writeNext)) {
      chapter.add(new Epubline(mode, writeNext, ""));
    }
  }


}
