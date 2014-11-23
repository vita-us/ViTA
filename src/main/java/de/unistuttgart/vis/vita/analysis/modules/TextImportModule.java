package de.unistuttgart.vis.vita.analysis.modules;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.importer.output.DocumentPartBuilder;
import de.unistuttgart.vis.vita.importer.txt.analyzers.AutomatedChapterDetection;
import de.unistuttgart.vis.vita.importer.txt.analyzers.FullTextChapterAnalyzer;
import de.unistuttgart.vis.vita.importer.txt.analyzers.MetadataAnalyzer;
import de.unistuttgart.vis.vita.importer.txt.input.Filter;
import de.unistuttgart.vis.vita.importer.txt.input.TextFileImporter;
import de.unistuttgart.vis.vita.importer.txt.input.TextSplitter;
import de.unistuttgart.vis.vita.importer.txt.util.TextImportResult;
import de.unistuttgart.vis.vita.importer.util.ChapterPosition;
import de.unistuttgart.vis.vita.importer.util.Line;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.TextSpan;

@AnalysisModule(weight = 0.1)
public class TextImportModule extends Module<ImportResult> {

  private Path filePath;
  private Boolean detectChapters;

  public TextImportModule(Path filePath) {
    this(filePath, true);
  }

  public TextImportModule(Path filePath, Boolean detectChapters) {
    this.filePath = filePath;
    this.detectChapters = detectChapters;
  }

  /**
   * {@inheritDoc}
   *
   * @throws InvalidPathException         If file is not txt
   * @throws FileNotFoundException        If file is not found.
   * @throws UnsupportedEncodingException If file encoding can not be detected, this can also happen
   *                                      if the file is empty or the file does not contain valid
   *                                      English text.
   * @throws SecurityException            If a security manager exists and its java.lang.SecurityManager.checkRead(java.lang.String)
   *                                      method denies read access to the file or directory
   */
  public ImportResult execute(ModuleResultProvider result, ProgressListener progressListener)
      throws FileNotFoundException, UnsupportedEncodingException {
    ImportResult importResult;
    TextSplitter textSplitter = new TextSplitter(importLines(filePath));
    DocumentMetadata documentMetadata = extractMetadata(textSplitter.getMetadataList(), filePath);
    DocumentPart documentPart = extractChapters(textSplitter.getTextList());
    setChapterRanges(documentPart);
    importResult = buildImportResult(documentMetadata, documentPart);
    return importResult;
  }
  
  /**
   * Sets the {@link Chapter#getRange()} property of all the chapters in the part, assuming the
   * document consists only of the one given part.
   * 
   * @param part the parts whose chapters to modify
   */
  private void setChapterRanges(DocumentPart part) {
    int currentPosition = 0;
    for (Chapter chapter : part.getChapters()) {
      chapter.setRange(new TextSpan(
          TextPosition.fromGlobalOffset(chapter, currentPosition),
          TextPosition.fromGlobalOffset(chapter, currentPosition + chapter.getLength())));
      currentPosition += chapter.getLength();
    }
  }

  /**
   * Gets the path to the txt-file and returns the data from the file.
   *
   * @param filePath Path - The path to the txt-file.
   * @return ArrayList of Line - The imported Lines.
   * @throws InvalidPathException         If file is not txt
   * @throws FileNotFoundException        If file is not found.
   * @throws UnsupportedEncodingException If file encoding can not be detected, this can also happen
   *                                      if the file is empty or the file does not contain valid
   *                                      English text.
   * @throws SecurityException            If a security manager exists and its java.lang.SecurityManager.checkRead(java.lang.String)
   *                                      method denies read access to the file or directory
   */
  private List<Line> importLines(Path filePath) throws FileNotFoundException,
      UnsupportedEncodingException {
    TextFileImporter importer = new TextFileImporter(filePath);
    Filter filter = new Filter(importer.getLines());
    return filter.filterEbookText();
  }

  /**
   * Extracts the metadata from the given lines.
   *
   * @param metadataLines ArrayList of Line - The lines containing the metadata area of the file.
   * @param filePath      Path - The path to the txt-file.
   * @return DocumentMetadata - The metadata extracted from the file.
   */
  private DocumentMetadata extractMetadata(List<Line> metadataLines, Path filePath) {
    MetadataAnalyzer metadataAnalyzer = new MetadataAnalyzer(metadataLines, filePath);
    return metadataAnalyzer.extractMetadata();
  }

  /**
   * Extracts the Chapters from the given lines.
   *
   * @param textLines ArrayList of Line - The lines containing the text area of the file.
   * @return DocumentPart - Contains ALL Chapters of the file.
   */
  private DocumentPart extractChapters(List<Line> textLines) {
    ChapterPosition chapterPosition;
    if (detectChapters) {
      AutomatedChapterDetection automatedChapterDetection =
          new AutomatedChapterDetection(textLines);
      chapterPosition = automatedChapterDetection.getChapterPosition();
    } else {
      FullTextChapterAnalyzer fullText = new FullTextChapterAnalyzer(textLines);
      chapterPosition = fullText.call();
    }
    DocumentPartBuilder documentPartBuilder = new DocumentPartBuilder(textLines, chapterPosition);
    return documentPartBuilder.call();
  }

  /**
   * Builds the ImportResult to return the extracted data.
   *
   * @param metadata DocumentMetadata - The extracted metadata of the file.
   * @param chapters DocumentPart - All extracted Chapters of the file in one DocumentPart.
   * @return ImportResult - The required ImportResult.
   */
  private ImportResult buildImportResult(DocumentMetadata metadata, DocumentPart chapters) {
    List<DocumentPart> list = new ArrayList<DocumentPart>();
    list.add(chapters);
    return new TextImportResult(list, metadata);
  }
}
