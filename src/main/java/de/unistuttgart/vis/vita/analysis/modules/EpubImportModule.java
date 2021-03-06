package de.unistuttgart.vis.vita.analysis.modules;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.importer.epub.extractors.AbstractEpubExtractor;
import de.unistuttgart.vis.vita.importer.epub.extractors.MetadataAnalyzerEpub;
import de.unistuttgart.vis.vita.importer.epub.input.EpubFileImporter;
import de.unistuttgart.vis.vita.importer.epub.input.EpubVersion;
import de.unistuttgart.vis.vita.importer.epub.input.EpubVersionDetector;
import de.unistuttgart.vis.vita.importer.epub.util.NoExtractorFoundException;
import de.unistuttgart.vis.vita.importer.output.BookBuilder;
import de.unistuttgart.vis.vita.importer.output.ImportResultImpl;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

@AnalysisModule
public class EpubImportModule extends Module<ImportResult> {

  Path filePath;

  public EpubImportModule(Path filePath) {
    this.filePath = filePath;
  }

  @Override
  public void observeProgress(double progress) {
    // do nothing
  }

  /**
   * {@inheritDoc}
   * 
   * @throws IOException Thrown if there is a problem while extracting data from the file.
   * @throws ParseException Thrown if metadata can not be parsed.
   * @throws NoExtractorFoundException Thrown of this epub-version can not be extracted.
   */
  public ImportResult execute(ModuleResultProvider result, ProgressListener progressListener)
      throws IOException, ParseException, NoExtractorFoundException {
    ImportResult importResult;
    EpubFileImporter importer = new EpubFileImporter(filePath);
    DocumentMetadata documentMetadata = extractMetadata(importer.getEbook(), filePath);
    List<DocumentPart> documentParts = extractChapters(importer.getEbook());
    importResult = buildImportResult(documentParts, documentMetadata);
    return importResult;
  }

  /**
   * Extracts the Metadata of the given book.
   * 
   * @param book The book from which the metadata should be extracted.
   * @param path The location of the book.
   * @return The metadata of the book.
   * @throws IOException Thrown if there is a problem while extracting data from the file.
   * @throws ParseException Thrown if metadata can not be parsed.
   */
  private DocumentMetadata extractMetadata(Book book, Path path) throws IOException, ParseException {
    MetadataAnalyzerEpub metadataAnalyzer = new MetadataAnalyzerEpub(book);
    return metadataAnalyzer.extractMetadata();
  }

  /**
   * Extracts the parts and chapters of the given book.
   * 
   * @param book The book from which the chapters should be extracted.
   * @return The chapters are stored in the parts.
   * @throws IOException Thrown if there is a problem while extracting data from the file, for
   *         example: File not found, does not fit to the epub-format,...
   * @throws NoExtractorFoundException Thrown of this epub-version can not be extracted.
   */
  private List<DocumentPart> extractChapters(Book book) throws IOException,
      NoExtractorFoundException {
    EpubVersionDetector versionDetector = new EpubVersionDetector(book);
    EpubVersion bookVersion = versionDetector.getVersion();
    AbstractEpubExtractor extractor = EpubVersion.getExtractorForVersion(bookVersion, book);
    BookBuilder bookBuilder =
        new BookBuilder(extractor.getPartList(), extractor.getChapterPositionList(),
            extractor.getTitleList());
    return bookBuilder.call();
  }

  /**
   * Builds an ImportResult out of the chapters/parts and the metadata of the book.
   * 
   * @param parts The parts containing the chapters of the book.
   * @param metadata The metadata of the book.
   * @return The ImportResult is the result of the module.
   */
  private ImportResult buildImportResult(List<DocumentPart> parts, DocumentMetadata metadata) {
    return new ImportResultImpl(parts, metadata);
  }
}
