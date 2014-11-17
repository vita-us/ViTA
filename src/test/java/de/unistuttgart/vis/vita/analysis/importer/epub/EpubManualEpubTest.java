package de.unistuttgart.vis.vita.analysis.importer.epub;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.Test;

import de.unistuttgart.vis.vita.analysis.modules.EpubImportModule;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.importer.epub.NoExtractorFoundException;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

/**
 * A primitive test which should work on Windows and exports the module result in a folder.
 */
public class EpubManualEpubTest {
  private final String SOURCE_PATH =
      "C:\\users\\sebastian\\dropbox\\uni-privat\\gutenbergepubs\\pg78.epub";
  private final String TARGET_FOLDER =
      "C:\\users\\sebastian\\dropbox\\uni-privat\\gutenbergepubs\\pg78\\";

  @Test
  public void test() throws URISyntaxException, FileNotFoundException, IOException, ParseException,
      NoExtractorFoundException {
    Path testPath = Paths.get(SOURCE_PATH);

    EpubImportModule module = new EpubImportModule(testPath);
    ImportResult result = module.execute(null, null);

    FileWriter mfw = new FileWriter(TARGET_FOLDER + "metadata" + ".txt");
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

    int partIndex = 1;
    for (DocumentPart part : result.getParts()) {
      int chapterIndex = 1;
      for (Chapter chapter : part.getChapters()) {
        FileWriter fw =
            new FileWriter(TARGET_FOLDER + "part" + partIndex + "chapter" + chapterIndex + ".txt");
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

  }

}
