package de.unistuttgart.vis.vita.importer.epub;

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
  private final Resource tocResource;
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
    addIds();
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

        for (int i = elements.indexOf(id) + 1; i < elements.size(); i++) {
          matcher = pattern.matcher(elements.get(i).text());
          if (!matcher.matches()) {
            partChaptersIds.add(elements.get(i).attr(Constants.ID));

          } else {
            break;
          }
        }
        partsWithChaptersIds.add(partChaptersIds);

      }
    }

    return partsWithChaptersIds;
  }

  /**
   * Extracts the chapter ids of the table of contents
   * 
   * @throws IOException
   */
  private void addIds() throws IOException {

    if (tocResource != null) {
      document = Jsoup.parse(contentBuilder.getStringFromInputStream(tocResource.getInputStream()));
      Elements navMaps = document.select(Constants.NAVMAP);
      if (!navMaps.isEmpty()) {
        Elements contents = navMaps.get(0).select(Constants.CONTENT);
        if (!contents.isEmpty()) {
          for (Element content : contents) {
            if (content.hasAttr(Constants.SOURCE)
                && content.attr(Constants.SOURCE).contains(Constants.PGEPUBID)) {
              String id = extractId(content.attr("src"));
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
   * Extracts the exact chapter id of the "src" element in the table of contents
   * 
   * @param input
   * @return
   */
  private String extractId(String input) {

    StringTokenizer stringTokenizer = new StringTokenizer(input, "#");
    while (stringTokenizer.hasMoreElements()) {
      stringTokenizer.nextToken();
      return stringTokenizer.nextToken();
    }
    return "";
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

  /**
   * Returns the chapters ids
   * 
   * @return
   */
  public List<String> getTocIds() {
    return tocIds;
  }
}
