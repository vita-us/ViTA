package de.unistuttgart.vis.vita.importer.epub;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import de.unistuttgart.vis.vita.importer.txt.util.ChapterPosition;
import de.unistuttgart.vis.vita.importer.txt.util.Line;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;

public class EpubTestImport {


  public void writer(Epub2Extractor extractor) throws IOException {
    FileWriter fw = new FileWriter("ausgabeRaw.txt");
    BufferedWriter bw = new BufferedWriter(fw);
    for (List<Line> parts : extractor.getPartList()) {
      int index = 0;
      for (Line line : parts) {
        bw.write(index + "   " + line.getText());
        bw.newLine();
        index++;
      }
    }
    bw.close();

    FileWriter fw1 = new FileWriter("ausgabePositions.txt");
    BufferedWriter bw1 = new BufferedWriter(fw1);
    for (ChapterPosition position : extractor.getChapterPositionList()) {
      for (int i = 1; i <= position.size(); i++) {
        bw1.write("number " + i + ": " + position.getStartOfHeading(i) + " "
            + position.getStartOfText(i) + " " + position.getEndOfText(i));
        bw1.newLine();
      }
    }
    bw1.write("TITLES");
    int i = 0;
    for (String title : extractor.getTitleList()) {
      bw1.write(i + ": " + title);
      bw1.newLine();
      i++;
    }

    bw1.close();
  }

  public static void main(String[] args) throws FileNotFoundException, IOException {
    EpubTestImport epubTestImport = new EpubTestImport();
    Path path = Paths.get("C:\\Users\\Sanjeev\\Documents\\Testdateien\\epubdateien\\pg1480.epub");
    EpubFileImporter epubFileImporter = new EpubFileImporter(path);
    Epub2Extractor epub2Extractor = new Epub2Extractor(epubFileImporter.getEbook());

//    DocumentMetadata doc = new DocumentMetadata();
//    doc = new MetadataAnalyzerEpub(epubFileImporter.getEbook(), path).extractMetadata();
//    System.out.println("title:"+ doc.getTitle());
//    System.out.println("author"+ doc.getAuthor());
//    System.out.println("edition:"+ doc.getEdition());
//    System.out.println("publisher:"+ doc.getPublisher());
//    System.out.println("publisher year:"+ doc.getPublishYear());
//    System.out.println("genre:"+ doc.getGenre());


    // List<List<Epubline>> chapters = new ArrayList<List<Epubline>>();
    // chapters = epub2Extractor.getChaptersList();

    // for (List<Epubline> chapter : chapters) {
    // System.out.println("first elem:" + "  " + "mode:" + chapter.get(0).getMode() + "  " + "text:"
    // + chapter.get(0).getEpubline());
    // System.out.println("second elem:" + "  " + "mode:" + chapter.get(1).getMode() + "  "
    // + "text:" + chapter.get(1).getEpubline());
    // System.out.println("third elem:" + "  " + "mode:" + chapter.get(2).getMode() + "  " + "text:"
    // + chapter.get(2).getEpubline());
    // System.out.println("last elem:" + "  " + "mode:" + chapter.get(chapter.size() - 1).getMode()
    // + "  " + "text:" + chapter.get(chapter.size() - 1).getEpubline());
    // System.out.println();
    //
    // }

    // List<List<Line>> parts = new ArrayList<List<Line>>();
    // parts = epub2Extractor.getPartList();
    // System.out.println("parts size:"+ parts.size());
    // for(int i = 0; i < 100; i++){
    // System.out.println("line:"+ parts.get(1).get(i).getText());
    // }
    //
    // List<ChapterPosition> chapterPostions = new ArrayList<ChapterPosition>();
    // chapterPostions = epub2Extractor.getChapterPositionList();
    // System.out.println("chap pos size:" + chapterPostions.size());
    //
    // List<String> partTitles = new ArrayList<String>();
    // partTitles = epub2Extractor.getTitleList();
    // System.out.println("part titles size:" + partTitles);

//     Epub3Extractor epub3Extractor = new Epub3Extractor(epubFileImporter.getEbook());
    List<List<Line>> part = new ArrayList<List<Line>>();

    epubTestImport.writer(epub2Extractor);

    // for(String line: epub3Extractor.test()){
    // System.out.println("line"+ line);
    // }
    // part = epub3Extractor.getPartList();
    //
    // for(int i = 0; i < 100; i++){
    // System.out.println("line:"+ part.get(0).get(i).getText());
    // }

    // System.out.println("chapter pos. size:"+
    // epub3Extractor.getChapterPositionList().get(2).size());
    //
    // System.out.println("part1 title"+ epub3Extractor.getTitleList().get(0));
    // System.out.println("part2 title"+ epub3Extractor.getTitleList().get(1));
    // System.out.println("part3 title"+ epub3Extractor.getTitleList().get(2));
    //
    // System.out.println("part size"+ part.size());
    // System.out.println("part1 first elem:"+ part.get(0).get(0).getText());
    // System.out.println("part1 last elem:"+ part.get(0).get(part.get(0).size()-2).getText());
    // System.out.println("part2 first:"+ part.get(1).get(0).getText());
    // System.out.println("part2 last elem:"+ part.get(1).get(part.get(1).size()-2).getText());
    // System.out.println("part3 first:"+ part.get(2).get(0).getText());
    // System.out.println("part3 last elem:"+ part.get(2).get(part.get(2).size()-2).getText());

  }
}
