package de.unistuttgart.vis.vita.importer.epub;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.zip.ZipInputStream;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

/**
 * Gets the book of the path and ZipInputStream
 * 
 *
 */
public class EpubFileImporter {

  private Path path;
  private ZipInputStream zipInputStream;

  /**
   * With the commited path the book will be fetched
   * @param newPath
   */
  public EpubFileImporter(Path newPath) {

    this.path = newPath;
  }

  /**
   * Returns the book of the path
   * @return
   * @throws IOException
   */
  public Book getEbook() throws IOException {
    Book ebook;
    EpubReader epubReader = new EpubReader();
    zipInputStream =
        new ZipInputStream(new FileInputStream(path.toFile()), Charset.forName("Cp437"));

    ebook = epubReader.readEpub(zipInputStream);
    zipInputStream.close();

    return ebook;
  }
}
