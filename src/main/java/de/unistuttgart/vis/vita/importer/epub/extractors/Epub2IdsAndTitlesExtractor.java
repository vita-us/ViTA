package de.unistuttgart.vis.vita.importer.epub.extractors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.siegmann.epublib.domain.Resource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Extracts ids of chapter Elements regarding part/parts in Epub2 and also the parts titles
 * 
 * 
 *
 */
public class Epub2IdsAndTitlesExtractor {

  private final List<Resource> resources;
  private Resource tocResource;
  private List<String> tocIds = new ArrayList<String>();
  private ContentBuilder contentBuilder = new ContentBuilder();
  private Pattern pattern = Pattern.compile(Constants.PART, Pattern.CASE_INSENSITIVE);
  private Matcher matcher;
  private Document document;

  /**
   * The commited book will be used in the methods below and the addIds() method will be called
   * 
   * @param resources
   * @param tocResource
   * @throws IOException
   */
  public Epub2IdsAndTitlesExtractor(List<Resource> resources, Resource tocResource)
      throws IOException {
    this.resources = resources;
    this.tocResource = tocResource;
    extractTocIds();
  }

  /**
   * Checks if the book contains a part
   * 
   * @return
   * @throws IOException
   */
  public boolean existsPart() throws IOException {

    for (Element id : getElementsIds()) {
      matcher = pattern.matcher(id.text());
      if (matcher.matches()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns jsoup Elements with the ids of the chapters
   * 
   * @return
   * @throws IOException
   */
  private List<Element> getElementsIds() throws IOException {
    List<Element> elementsIds = new ArrayList<Element>();
    Map<String, String> map = new HashMap<String, String>();

    for (Resource resource : resources) {
      document = Jsoup.parse(contentBuilder.getStringFromInputStream(resource.getInputStream()));
      for (String id : tocIds) {
        if (document.getElementById(id) != null && !map.containsKey(id)) {
          elementsIds.add(document.getElementById(id));
          map.put(id, document.getElementById(id).text());
        }
      }
    }
    return elementsIds;
  }

  /**
   * Returns the List<List<String>> which contains the correct chapter ids for each part
   * 
   * @return List<List<String>>
   * @throws IOException
   */
  public List<List<String>> getPartsChaptersIds() throws IOException {
    List<List<String>> partsWithChaptersIds = new ArrayList<List<String>>();

    List<Element> elements = getElementsIds();

    for (Element id : elements) {
      matcher = pattern.matcher(id.text());
      if (matcher.matches()) {
        List<String> partChaptersIds = new ArrayList<String>();
        addIdToList(elements, id, partChaptersIds);
        partsWithChaptersIds.add(partChaptersIds);

      }
    }

    return partsWithChaptersIds;
  }

  /**
   * Adds the id to partchapter list
   * 
   * @param elements
   * @param id
   * @param partChaptersIds
   */
  private void addIdToList(List<Element> elements, Element id, List<String> partChaptersIds) {
    for (int i = elements.indexOf(id) + 1; i < elements.size(); i++) {
      matcher = pattern.matcher(elements.get(i).text());
      if (!matcher.matches()) {
        partChaptersIds.add(elements.get(i).attr(Constants.ID));
      } else {
        break;
      }
    }
  }

  private void extractTocIds() throws IOException {
    if (notEveryElementHasId()) {

      for (Resource resource : resources) {
        document = Jsoup.parse(contentBuilder.getStringFromInputStream(resource.getInputStream()));
        if (!document.getAllElements().isEmpty()) {
          Elements allElements = document.getAllElements();
          for (Element currentElement : allElements) {
            if (!currentElement.attr(Constants.ID).isEmpty()
                && !currentElement.text().matches(Constants.PART)
                && !currentElement.tagName().matches(Constants.DIV)
                && !currentElement.hasAttr("href") && !currentElement.text().isEmpty()) {
              String id = currentElement.attr(Constants.ID);
              if (!tocIds.contains(id)) {
                tocIds.add(id);
              }
            }
          }
        }
      }
    } else {
      addNcxIdsToList();
    }
  }

  private void addNcxIdsToList() throws IOException {
    if (tocResource != null) {
      document = Jsoup.parse(contentBuilder.getStringFromInputStream(tocResource.getInputStream()));
      Elements navMaps = document.select(Constants.NAVMAP);
      if (!navMaps.isEmpty()) {
        Elements contents = navMaps.get(0).select(Constants.CONTENT);
        if (!contents.isEmpty()) {
          for (Element content : contents) {

            if (content.hasAttr(Constants.SOURCE)
                && (content.attr(Constants.SOURCE).contains(Constants.PGEPUBID) || content.attr(
                    Constants.SOURCE).contains(Constants.ID))) {
              String id = extractId(content.attr(Constants.SOURCE));
              if (!tocIds.contains(id)) {
                tocIds.add(id);
              }
            }
          }
        }
      }
    }
  }

  /**
   * Checks if every element in a document has a id
   * 
   * @return
   * @throws IOException
   */
  private boolean notEveryElementHasId() throws IOException {
    for (Resource resource : resources) {
      document = Jsoup.parse(contentBuilder.getStringFromInputStream(resource.getInputStream()));
      if (!document.getAllElements().isEmpty()) {
        Elements allElements = document.getAllElements();
        if (getFirstIdElement(allElements) != null) {
          Element firstIdElement = getFirstIdElement(allElements);
          for (int i = allElements.indexOf(firstIdElement); i < allElements.size(); i++) {

            if (!allElements.get(i).hasAttr(Constants.ID)) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  private Element getFirstIdElement(Elements allElements) {
    Element firstIdElement = null;
    for (Element currentElement : allElements) {
      if (!currentElement.attr(Constants.ID).isEmpty()) {
        firstIdElement = currentElement;
      }
    }
    return firstIdElement;
  }

  /**
   * Returns the titles of the parts
   * 
   * @return
   * @throws IOException
   */
  public List<String> getPartsTitles() throws IOException {
    List<String> partsTitles = new ArrayList<String>();
    List<Element> elementsIds = getElementsIds();

    for (Element id : elementsIds) {
      matcher = pattern.matcher(id.text());
      if (matcher.matches()) {
        partsTitles.add(id.text());
      }
    }
    return partsTitles;
  }

  private String extractId(String input) {

    StringTokenizer stringTokenizer = new StringTokenizer(input, "#");
    while (stringTokenizer.hasMoreElements()) {
      stringTokenizer.nextToken();
      return stringTokenizer.nextToken();
    }
    return "";
  }

  /**
   * Returns the chapters ids
   * 
   * @return
   */
  public List<String> getTocIds() {
    return tocIds;
  }
}
