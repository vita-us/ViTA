package de.unistuttgart.vis.vita.importer.epub;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import de.unistuttgart.vis.vita.model.document.DocumentMetadata;

public class TestEpub {

  public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
    Path testPath =
        Paths.get("C:\\Users\\Sanjeev\\Documents\\Testdateien\\epubdateien\\pg1480.epub");
    EpubFileImporter epubFileImporter = new EpubFileImporter(testPath);
    MetadataAnalyzerEpub metadataAnalyzerEpub =
        new MetadataAnalyzerEpub(epubFileImporter.getEbook(), testPath);
    DocumentMetadata documentMetadata = new DocumentMetadata();
    documentMetadata = metadataAnalyzerEpub.extractMetadata();
    
    System.out.println("Title:" + " " + "<" + documentMetadata.getTitle() + ">");
    System.out.println("Author:" + " " + "<" + documentMetadata.getAuthor() + ">");
    System.out.println("PublishYear:" + " " + "<" + documentMetadata.getPublishYear() + ">");
    System.out.println("Publisher:" + " " + "<" + documentMetadata.getPublisher() + ">");
    System.out.println("Edition:" + " " + "<" + documentMetadata.getEdition() + ">");
    System.out.println("Genre:" + " " + "<" + documentMetadata.getGenre() + ">");

  }
}
