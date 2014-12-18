package de.unistuttgart.vis.vita.analysis.importer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.modules.EpubImportModule;
import de.unistuttgart.vis.vita.analysis.modules.TextImportModule;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.importer.epub.util.NoExtractorFoundException;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

/**
 * A primitive test which should work on Windows and exports the module result in a folder. For a
 * given folder, this class will create result-subfolders for each file of the given type in it and analyze the
 * files with an ImportModule.
 */
public class ManualImportModuleTest {
  // ImportType
  private final static ImportType IMPORT_TYPE = ImportType.TXT;

  // Folder Paths
  private static final String TXT_FOLDER_PATH =
      "C:\\users\\sebastian\\dropbox\\uni-privat\\gutenbergtxts\\new books\\";
  private static final String EPUB_FOLDER_PATH =
      "C:\\users\\sebastian\\dropbox\\uni-privat\\gutenbergepubs\\";

  // Test attributes
  private final List<String> FILE_NAMES = new ArrayList<String>();
  private final String FOLDER_PATH;
  private final String FILE_EXTENSION;
  private final String FOLDER_SLASH = "\\";
  private Module<ImportResult> module;

  private enum ImportType {
    TXT, EPUB;
  }

  public ManualImportModuleTest() {
    if (IMPORT_TYPE.equals(ImportType.TXT)) {
      FILE_EXTENSION = ".txt";
      FOLDER_PATH = TXT_FOLDER_PATH;
    } else {
      FILE_EXTENSION = ".epub";
      FOLDER_PATH = EPUB_FOLDER_PATH;
    }
  }

  @Before
  public void setUp() {
    // add all files with the FILE_EXTENSION to FILE_NAMES
    File folderPathFile = new File(FOLDER_PATH);
    for (File file : folderPathFile.listFiles()) {
      if (file.getName().endsWith(FILE_EXTENSION)) {
        int fileNameLengthWithoutExtension = file.getName().length() - FILE_EXTENSION.length();
        String fileName = file.getName().substring(0, fileNameLengthWithoutExtension);
        FILE_NAMES.add(fileName);
      }
    }
  }

  @Test
  public void test() throws URISyntaxException, FileNotFoundException, IOException, ParseException,
      NoExtractorFoundException {
    for (String fileName : FILE_NAMES) {
      System.out.println("Building Book : " + fileName + " (" + (FILE_NAMES.indexOf(fileName) + 1)
          + "/" + FILE_NAMES.size() + ")");

      try {
        // build required paths
        String sourcePath = FOLDER_PATH + fileName + FILE_EXTENSION;
        String targetFolder = FOLDER_PATH + fileName;
        Path testPath = Paths.get(sourcePath);

        // execute module
        ImportResult result = useImportModule(testPath);

        // delete old result folder, make a new one
        File targetFolderFile = new File(targetFolder);
        FileUtils.deleteDirectory(targetFolderFile);
        targetFolderFile.mkdir();

        // add results to folder

        // make metadata file
        FileWriter mfw = new FileWriter(targetFolder + FOLDER_SLASH + "metadata" + ".txt");
        BufferedWriter mbw = new BufferedWriter(mfw);
        mbw.write("Filename: " + testPath.getFileName());
        mbw.newLine();
        mbw.write("Title: " + result.getMetadata().getTitle());
        mbw.newLine();
        mbw.write("Author: " + result.getMetadata().getAuthor());
        mbw.newLine();
        mbw.write("Edition: " + result.getMetadata().getEdition());
        mbw.newLine();
        mbw.write("Genre: " + result.getMetadata().getGenre());
        mbw.newLine();
        mbw.write("Publisher: " + result.getMetadata().getPublisher());
        mbw.newLine();
        mbw.write("Publish Year: " + result.getMetadata().getPublishYear());
        mbw.newLine();
        mbw.close();

        // make one file per chapter
        int partIndex = 1;
        for (DocumentPart part : result.getParts()) {
          int chapterIndex = 1;
          for (Chapter chapter : part.getChapters()) {
            FileWriter fw =
                new FileWriter(targetFolder + FOLDER_SLASH + "part" + partIndex + "chapter"
                    + chapterIndex + ".txt");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("//=============================\\\\");
            bw.newLine();
            String partTitle = part.getTitle().replaceAll("\n", "\r\n");
            bw.write(partTitle);
            bw.newLine();
            bw.write("//=============================\\\\");
            bw.newLine();

            String text = chapter.getText().replaceAll("\n", "\r\n");
            bw.write("-----------------------------");
            bw.newLine();
            String chapterTitle = chapter.getTitle().replaceAll("\n", "\r\n");
            bw.write(chapterTitle);
            bw.newLine();
            bw.write("-----------------------------");
            bw.newLine();
            bw.write(text);
            bw.newLine();
            bw.close();
            chapterIndex++;
          }
          partIndex++;
        }
      } catch (Exception e) {
        System.out.println("IMPORT FAILED: " + fileName);
      }
    }
  }


  /**
   * Decides which Importer should be used.
   * 
   * @param path
   * @return
   * @throws Exception
   */
  private ImportResult useImportModule(Path path) throws Exception {
    if (IMPORT_TYPE == ImportType.TXT) {
      module = new TextImportModule(path);
    } else {
      module = new EpubImportModule(path);
    }
    return module.execute(null, null);
  }

}
