package de.unistuttgart.vis.vita.importer.epub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Epub2IdsAndTitlesExtractor {

  private Book book = new Book();
  private List<String> tocIds = new ArrayList<String>();
  private ContentBuilder contentBuilder = new ContentBuilder();
  private Pattern pattern = Pattern.compile(Constants.PART, Pattern.CASE_INSENSITIVE);
  private Matcher matcher;
  private Document document;

  public Epub2IdsAndTitlesExtractor(Book newBook) throws IOException {
    this.book = newBook;
    addIds();

  }

  public boolean existsPart() throws IOException {

    for (Element id : getElementsIds()) {
      matcher = pattern.matcher(id.text());
      if (matcher.matches()) {
        return true;
      }
    }
    return false;
  }

  private List<Element> getElementsIds() throws IOException {
    List<Element> elementsIds = new ArrayList<Element>();
    Map<String, String> map = new HashMap<String, String>();

    for (Resource resource : book.getContents()) {
      document = Jsoup.parse(contentBuilder.getStringFromInputStream(resource.getInputStream()));
      for (String id : tocIds) {
        if (document.getElementById(id) != null) {
          if (!map.containsKey(id)) {
            elementsIds.add(document.getElementById(id));
            map.put(id, document.getElementById(id).text());
          }
        }
      }
    }
    return elementsIds;
  }

  public List<List<String>> getPartsChaptersIds() throws IOException {
    List<List<String>> partsWithChaptersIds = new ArrayList<List<String>>();

    List<Element> elements = new ArrayList<Element>();
    elements = getElementsIds();

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

  private void addIds() throws IOException {

    if (book.getNcxResource() != null) {
      document =
          Jsoup.parse(contentBuilder.getStringFromInputStream(book.getNcxResource()
              .getInputStream()));
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

  private String extractId(String input) {

    StringTokenizer stringTokenizer = new StringTokenizer(input, "#");
    while (stringTokenizer.hasMoreElements()) {
      stringTokenizer.nextToken();
      return stringTokenizer.nextToken();
    }
    return "";
  }

  public List<String> getPartsTitles() throws IOException {
    List<String> partsTitles = new ArrayList<String>();
    List<Element> elementsIds = new ArrayList<Element>();
    elementsIds = getElementsIds();

    for (Element id : elementsIds) {
      matcher = pattern.matcher(id.text());
      if (matcher.matches()) {
        partsTitles.add(id.text());
      }
    }
    return partsTitles;
  }

  public List<String> getTocIds() {
    return tocIds;
  }
}
