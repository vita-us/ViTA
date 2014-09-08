package de.unistuttgart.vis.vita.importer.txt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple Class to import txt-files and output an ArrayList of Lines. The Encoding should be ASCII
 * or UTF-8, but UTF-16, UTF-16BE, UTF-16LE and ISO-8859-1 should also work well for English texts. <br/>
 * <br/>
 * 
 * Use the Constructor for import: <br/>
 * TextFileImporter(Path path) <br/>
 * <br/>
 * 
 * Get the list: <br/>
 * getLines() <br/>
 * <br/>
 * 
 * Preconditions: <br/>
 * Of course the file should exist and must be readable. Empty files or files only containing
 * special signs can cause exceptions. This class is build to work on English texts and the common
 * English letters and numbers. Texts in other languages can cause problems in the automated
 * encoding detection.
 * 
 */
public class TextFileImporter {
  private Path path;
  private ArrayList<Line> lines;
  private ArrayList<Charset> charsets;
  private Charset usedCharset = null;

  /**
   * Will create a list of lines from the txt-file at the given path. Please note that the file must
   * exist, be a txt file, be readable, not empty and contain English text.
   * 
   * @param path
   * @throws IllegalArgumentException, if file is not txt
   * @throws FileNotFoundException If file is not a file or does not exist
   * @throws IllegalStateException If file is not readable, or if file encoding can not be detected,
   *         this can also happen if the file is empty or the file does not contain valid English
   *         text.
   * @throws SecurityException
   */
  public TextFileImporter(Path path) throws IllegalArgumentException, FileNotFoundException,
      IllegalStateException, SecurityException {
    super();
    this.path = path;
    initializeCharsets();
    checkFileName();
    checkFile();
    lines = importData(path);
  }

  /**
   * The extracted lines of the file.
   */
  public ArrayList<Line> getLines() {
    return lines;
  }

  /**
   * Get the String representation of the charset used for the encoding of the file.
   * 
   * @return String: The name of the detected encoding. Or 'no charset' if none was the right one.
   */
  public String getNameOfDetectedEncoding() {
    if (usedCharset != null) {
      return usedCharset.displayName();
    } else {
      return "no charset";
    }
  }

  /**
   * Sets the possible charsets for the encoding detection
   */
  private void initializeCharsets() {
    charsets = new ArrayList<Charset>();
    // order is important
    charsets.add(StandardCharsets.US_ASCII);
    charsets.add(StandardCharsets.UTF_8);
    charsets.add(StandardCharsets.UTF_16);
    charsets.add(StandardCharsets.UTF_16BE);
    charsets.add(StandardCharsets.UTF_16LE);
    charsets.add(StandardCharsets.ISO_8859_1);
    // last charset is null, it symbolizes 'nothing found'
    charsets.add(null);
  }

  /**
   * Checks if the file at the given path exists, is a file and is readable.
   * 
   * @throws FileNotFoundException If file is not a file or does not exist
   * @throws IllegalStateException If file is not readable
   * @throws SecurityException If a security manager exists and its
   *         java.lang.SecurityManager.checkRead(java.lang.String) method denies read access to the
   *         file or directory
   */
  private void checkFile() throws FileNotFoundException, IllegalStateException, SecurityException {
    File file = path.toFile();
      checkFileExists(file);
      checkIsFile(file);
      checkFileIsReadable(file);
  }

  /**
   * Assures the ending of the file is ".txt"
   * 
   * @throws IllegalArgumentException if the ending is not ".txt"
   */
  private void checkFileName() throws IllegalArgumentException {
    String nameOfFile = path.getFileName().toString();
    if (!(nameOfFile.length() > 4) || !(nameOfFile.endsWith(".txt"))) {
      throw new IllegalArgumentException("No txt-file-ending or missing name: " + path.toString());
    }
  }

  /**
   * Assures the file exists.
   * 
   * @param file
   * @throws FileNotFoundException If file does not exist
   * @throws SecurityException If a security manager exists and its
   *         java.lang.SecurityManager.checkRead(java.lang.String) method denies read access to the
   *         file or directory
   */
  private void checkFileExists(File file) throws FileNotFoundException, SecurityException {
    if (!file.exists()) {
      throw new FileNotFoundException("File not found: " + path.toString());
    }
  }

  /**
   * Assures the file is really a file.
   * 
   * @param file
   * @throws FileNotFoundException If file is not a file
   * @throws SecurityException If a security manager exists and its
   *         java.lang.SecurityManager.checkRead(java.lang.String) method denies read access to the
   *         file
   */
  private void checkIsFile(File file) throws FileNotFoundException, SecurityException {
    if (!file.isFile()) {
      throw new FileNotFoundException("Is not a file: " + path.toString());
    }
  }

  /**
   * Assures the file is readable.
   * 
   * @param file
   * @throws IllegalStateException If file is not readable
   * @throws SecurityException If a security manager exists and its
   *         java.lang.SecurityManager.checkRead(java.lang.String) method denies read access to the
   *         file
   */
  private void checkFileIsReadable(File file) throws IllegalStateException, SecurityException {
    if (!file.canRead()) {
      boolean succesfullyChangedToReadable = file.setReadable(true);
      if (!succesfullyChangedToReadable) {
        throw new IllegalStateException("File not readable: " + path.toString());
      }
    }
  }

  /**
   * Gets the correct encoding and scans the file for lines.
   * 
   * @param path
   * @return
   * @throws IllegalStateException If file encoding can not be detected, this can also happen if the
   *         file is empty or the file does not contain valid english text.
   */
  private ArrayList<Line> importData(Path path) throws IllegalStateException {
    ArrayList<Line> lines = new ArrayList<Line>();
    BufferedReader reader = null;
    Iterator<Charset> charsetIterator = this.charsets.iterator();
    Boolean successfullyReadIn = false;

    while (!successfullyReadIn && charsetIterator.hasNext()) {
      try {
        // try next encoding
        this.usedCharset = charsetIterator.next();
        if (usedCharset == null) {
          break;
        }
        reader = initializeReader(path, this.usedCharset);

        // throws Exception if encoding is wrong
        lines = createList(reader);

        // false if encoding is wrong (or file is empty)
        successfullyReadIn = !fileDataIsEmpty(lines);
      } catch (IOException e) {
        // get next charset
      } finally {
        closeReader(reader);
      }
    }
    if (successfullyReadIn) {
      return lines;
    } else {
      throw new IllegalStateException("Unknown File Encoding or File Empty: " + path.toString());
    }
  }

  /**
   * Adds every line of the file to the list of lines. Special symbols at the beginning of the first
   * line will be deleted.
   * 
   * @param reader BufferedReader, the reader for the txt-file to import
   * @return ArrayList<Line>, all lines from the file
   * @throws IOException If an I/O error occurs. Thrown by the Reader.
   */
  private ArrayList<Line> createList(BufferedReader reader) throws IOException {
    ArrayList<Line> lines = new ArrayList<Line>();
    String lineText;

    while ((lineText = reader.readLine()) != null) {
      Line aNewline = new Line(lineText);
      lines.add(aNewline);
    }

    // sometimes special symbols are read in at the beginning, we are only interested in real
    // words, so we can filter them out
    deleteNonTextStartSymbols(lines);

    return lines;
  }

  /**
   * Creates a Buffered Reader from a path and a choosen charset.
   * 
   * @param path Path
   * @param charset Charset, for encoding
   * @return BufferedReader
   * @throws IOException If an I/O error occurs opening the file.
   */
  private BufferedReader initializeReader(Path path, Charset charset) throws IOException {
    return Files.newBufferedReader(path, charset);
  }

  /**
   * Closes the Reader.
   * 
   * @param reader
   * @return boolean, true if closed
   */
  private boolean closeReader(BufferedReader reader) {
    boolean closed = false;
    try {
      if (reader != null) {
        reader.close();
        closed = true;
      }
    } catch (IOException e) {
      // do nothing; log?
    }
    return closed;
  }

  /**
   * If lines is not empty, all special symbols will be deleted from the beginning of the first
   * line.
   * 
   * @param lines
   */
  private void deleteNonTextStartSymbols(ArrayList<Line> lines) {
    String specialSymbols = "[^\\s\\w\"#']";
    String manySpecialSymbolsAtTheBeginning = "^" + specialSymbols + "*";

    if (lines.size() > 0) {
      String firstLine = lines.get(0).getText();
      Matcher matcher = Pattern.compile(manySpecialSymbolsAtTheBeginning).matcher(firstLine);
      StringBuffer stringBuffer = new StringBuffer(firstLine.length());

      // Delete occurence
      while (matcher.find()) {
        matcher.appendReplacement(stringBuffer, "");
      }
      matcher.appendTail(stringBuffer);
      lines.get(0).setText(stringBuffer.toString());
    }
  }

  /**
   * Analyzes the content of the file and determines whether the file is considered empty or not.
   * 
   * @param lines
   * @return Boolean, true if file is empty or there are no visible symbols
   */
  private boolean fileDataIsEmpty(ArrayList<Line> lines) {
    return lines.isEmpty() || allLinesAreWhitespace(lines);
  }

  /**
   * Searches for visible symbols in the file.
   * 
   * @param lines
   * @return Boolean, true if there are no visible symbols
   */
  private boolean allLinesAreWhitespace(ArrayList<Line> lines) {
    boolean allCheckedLinesAreWhitespace = true;
    Pattern onlyWhitespacePattern = Pattern.compile("^\\s*$");

    for (Line line : lines) {
      if (allCheckedLinesAreWhitespace) {
        Matcher matcher = onlyWhitespacePattern.matcher(line.getText());
        allCheckedLinesAreWhitespace = matcher.matches();
      } else {
        break;
      }
    }
    return allCheckedLinesAreWhitespace;
  }
}
