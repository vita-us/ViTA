package de.unistuttgart.vis.vita.importer.epub;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.zip.ZipInputStream;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;


public class EpubFileImporter {

  private Path path;
  private ZipInputStream zipInputStream;

  public EpubFileImporter(Path newPath) {

    this.path = newPath;
  }

  public Book getEbook() throws FileNotFoundException, IOException {
    Book ebook = null;
    EpubReader epubReader = new EpubReader();
    zipInputStream =
        new ZipInputStream(new FileInputStream(path.toFile()), Charset.forName("Cp437"));

    ebook = epubReader.readEpub(zipInputStream);
    zipInputStream.close();

    return ebook;
  }
}
