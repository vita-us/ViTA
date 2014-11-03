package de.unistuttgart.vis.vita.importer.epub;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import nl.siegmann.epublib.domain.Book;

/**
 * For a given epub-file, this class tries to detect the version of the file.
 */
public class EpubVersionDetector {

  private Book book;
  private org.jsoup.nodes.Document document;
  private ContentBuilder contentBuilder = new ContentBuilder();

  /**
   * Initializes the version detector to analyze the book. The book can not be changed later.
   * 
   * @param book The book to analyze.
   */
  public EpubVersionDetector(Book book) {
    this.book = book;
  }

  /**
   * Get the version of the book.
   * 
   * @return The version of the book.
   */
  public EpubVersion getVersion() throws IOException {
    EpubVersion version = EpubVersion.UNKNOWN;
    if (!(book == null)) {
      document =
          Jsoup.parse(contentBuilder.getStringFromInputStream(book.getOpfResource()
              .getInputStream()));

      // the version can be found in 'package'
      Element element = document.select("package").first();

      // check text at 'version' and set correct EpubVersion
      if (element.attr("version").toLowerCase().contains("2.0")) {
        version = EpubVersion.STANDARD2;
      } else if (element.attr("version").toLowerCase().contains("3.0")) {
        version = EpubVersion.STANDARD3;
      }
    }
    return version;
  }

}
