package de.unistuttgart.vis.vita.model;

import de.unistuttgart.vis.vita.model.document.Chapter;

import org.apache.lucene.store.Directory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 * Manages texts for the model.
 */
public class TextRepository {
  private static final String CHAPTER_ID = "chapterId";
  private static final String CHAPTER_TEXT = "chapterText";

  // list of directories
  private List<Directory> indexes = new ArrayList<Directory>();
  private IndexReader indexReader;

  /**
   * Sets the text of the commited chapter with the related chapter text of lucene index
   * 
   * @param chapterToPopulate
   * @throws IOException
   */
  public void populateChapterText(Chapter chapterToPopulate) throws IOException {

    for (Directory index : indexes) {
      indexReader = IndexReader.open(index);

      // iteration till to the maximum number of documents in an index
      for (int i = 0; i < indexReader.maxDoc(); i++) {

        // if chapter id in the document of the index is equal to the chapterToPopulate id
        // then set the text of chapterToPopulate with the chapter text of the current chapter of
        // this document
        if (indexReader.document(i).getField(CHAPTER_ID).stringValue()
            .equals(chapterToPopulate.getId())) {
          chapterToPopulate.setText(indexReader.document(i).getField(CHAPTER_TEXT).stringValue());
          break;
        }
      }
    }
  }

  /**
   * Stores a list of chapters of an ebook in lucene
   * 
   * @param chaptersToStore
   * @throws IOException
   */
  public void storeChaptersTexts(List<Chapter> chaptersToStore) throws IOException {
    IndexWriterConfig config =
        new IndexWriterConfig(Version.LUCENE_CURRENT, new StandardAnalyzer());
    Directory index = new RAMDirectory();
    IndexWriter indexWriter = new IndexWriter(index, config);
    for (Chapter chapter : chaptersToStore) {
      indexWriter.addDocument(addFieldsToDocument(chapter));
    }
    // at the created index along with its documents to the indexes list
    indexes.add(index);
    indexWriter.close();
  }

  /**
   * 
   * @return the indexes
   */
  public List<Directory> getIndexes() {
    return indexes;
  }

  /**
   * Creates a document and adds the chapter id and chapter text field to this
   * 
   * @param chapterToStore
   * @return
   */
  private Document addFieldsToDocument(Chapter chapterToStore) {
    Document document = new Document();
    document.add(new Field(CHAPTER_ID, chapterToStore.getId(), TextField.TYPE_STORED));
    document.add(new Field(CHAPTER_TEXT, chapterToStore.getText(), TextField.TYPE_STORED));
    return document;
  }
}
