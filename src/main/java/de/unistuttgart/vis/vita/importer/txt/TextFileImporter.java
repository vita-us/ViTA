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

public class TextFileImporter {
  private Path path;
  private ArrayList<Line> result;
  private ArrayList<Charset> charsets;
  private Charset usedCharset;

  private void initialiseCharsets() {
    charsets = new ArrayList<Charset>();
    charsets.add(StandardCharsets.US_ASCII);
    charsets.add(StandardCharsets.UTF_8);
    charsets.add(StandardCharsets.UTF_16);
    charsets.add(StandardCharsets.UTF_16BE);
    charsets.add(StandardCharsets.UTF_16LE);
    charsets.add(StandardCharsets.ISO_8859_1);


  }

  protected TextFileImporter(Path path) throws IllegalArgumentException, FileNotFoundException,
      IllegalStateException, SecurityException {
    super();
    this.path = path;
    initialiseCharsets();
    checkFileName();
    checkFile();
    result = importData(path);
  }

  protected ArrayList<Line> getResult() {
    return result;
  }

  protected String getNameOfDetectedEncoding() {
    return usedCharset.displayName();
  }

  private void checkFileName() throws IllegalArgumentException {
    String nameOfFile = path.getFileName().toString();
    if (!(nameOfFile.length() > 4) || !(nameOfFile.endsWith(".txt"))) {
      throw new IllegalArgumentException("No txt-file-ending or missing name: " + path.toString());
    }
  }

  private void checkFile() throws FileNotFoundException, IllegalStateException, SecurityException {
    File file = path.toFile();
    try {
      checkFileExists(file);
      checkIsFile(file);
      checkFileIsReadable(file);
    } catch (SecurityException e) {
      throw new SecurityException("Import blocked by another service");
    }
  }

  private void checkFileExists(File file) throws FileNotFoundException, SecurityException {
    if (!file.exists()) {
      throw new FileNotFoundException("File not found: " + path.toString());
    }
  }

  private void checkIsFile(File file) throws FileNotFoundException, SecurityException {
    if (!file.isFile()) {
      throw new FileNotFoundException("Is not a file: " + path.toString());
    }
  }

  private void checkFileIsReadable(File file) throws IllegalStateException, SecurityException {
    if (!file.canRead()) {
      boolean succesfullyChangedToReadable = file.setReadable(true);
      if (!succesfullyChangedToReadable) {
        throw new IllegalStateException("File not readable: " + path.toString());
      }
    }
  }

  private ArrayList<Line> importData(Path path) throws IllegalStateException {
    ArrayList<Line> result = new ArrayList<Line>();
    BufferedReader reader = null;
    Boolean successfullyReadIn = false;
    Iterator<Charset> charsetIterator = charsets.iterator();
    while (!successfullyReadIn && charsetIterator.hasNext()) {
      try {
        usedCharset = charsetIterator.next();
        reader = initializeReader(path, usedCharset);
        result = createList(reader);
        deleteNonTextStartSymbols(result);
        if (!fileDataIsEmpty(result)) {
          successfullyReadIn = true;
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        closeReader(reader);
      }
    }
    if (successfullyReadIn) {
      return result;
    } else {
      throw new IllegalStateException("Unknown File Encoding or File Empty: " + path.toString());
    }
  }

  private ArrayList<Line> createList(BufferedReader reader) throws IOException {
    ArrayList<Line> result = new ArrayList<Line>();
    String lineText;// = reader.readLine();
    while ((lineText = reader.readLine()) != null) {
      Line aNewline = new Line(lineText);
      result.add(aNewline);
      System.out.println(lineText);
    }
    return result;
  }

  private BufferedReader initializeReader(Path path, Charset charset) throws IOException {
    BufferedReader reader = null;
    reader = Files.newBufferedReader(path, charset);
    return reader;
  }

  private boolean closeReader(BufferedReader reader) {
    boolean closed = false;
    try {
      reader.close();
      closed = true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return closed;
  }

  private void deleteNonTextStartSymbols(ArrayList<Line> fileData) {
    if (fileData.size() > 0) {

      String firstLine = fileData.get(0).getText();

      String specialSymbols = "[^\\s\\w\"#']";
      String manySpecialSymbolsAtTheBeginning = "^" + specialSymbols + "*";
      Matcher matcher = Pattern.compile(manySpecialSymbolsAtTheBeginning).matcher(firstLine);

      StringBuffer stringBuffer = new StringBuffer(firstLine.length());

      while (matcher.find())
        matcher.appendReplacement(stringBuffer, "");

      matcher.appendTail(stringBuffer);

      fileData.get(0).setText(stringBuffer.toString());

    }
  }

  private boolean fileDataIsEmpty(ArrayList<Line> fileData) {
    return fileData.isEmpty() || allLinesAreWhitespaceOrEmpty(fileData);
  }

  private boolean allLinesAreWhitespaceOrEmpty(ArrayList<Line> fileData) {
    boolean allCheckedLinesAreWhitespace = true;
    Pattern onlyWhitespacePattern = Pattern.compile("^\\s*$");

    for (Line line : fileData) {
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
