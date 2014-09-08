package de.unistuttgart.vis.vita.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.Version;

import de.unistuttgart.vis.vita.model.document.Chapter;

/**
 * Manages texts for the model.
 */
public class TextRepository {
  private static final String CHAPTER_ID = "chapterId";
  private static final String CHAPTER_TEXT = "chapterText";
  private static final String INDEX_PATH = "~/.vita/lucene/";
  private static final Version LUCENE_VERSION = Version.LUCENE_4_10_0;

  // list of directories
  private List<Directory> indexes = new ArrayList<Directory>();

  /**
   * Sets the text of the commited chapter with the related chapter text of lucene index
   * 
   * @param chapterToPopulate
   * @throws IOException
   * @throws ParseException
   */
  public void populateChapterText(Chapter chapterToPopulate) throws IOException, ParseException {
    Directory index =
        FSDirectory.open(new File(INDEX_PATH + chapterToPopulate.getDocument().getId()));
    IndexReader indexReader = DirectoryReader.open(index);
    IndexSearcher indexSearcher = new IndexSearcher(indexReader);
    QueryParser queryParser =
        new QueryParser(CHAPTER_ID , new StandardAnalyzer());
    Query query = queryParser.parse(chapterToPopulate.getId());
    ScoreDoc[] hits = indexSearcher.search(query, 1).scoreDocs;
    Document hitDoc = indexSearcher.doc(hits[0].doc);
    chapterToPopulate.setText(hitDoc.getField(CHAPTER_TEXT).stringValue());
  }

  /**
   * Stores a list of chapters of an ebook in a lucene directory
   * 
   * @param chaptersToStore
   * @throws IOException
   */
  public void storeChaptersTexts(List<Chapter> chaptersToStore) throws IOException {
    String documentId = chaptersToStore.get(0).getDocument().getId();
    for (Chapter chapterToStore : chaptersToStore) {
      if (!chapterToStore.getDocument().getId().equals(documentId)) {
        throw new IllegalArgumentException();
      }
    }
    IndexWriterConfig config = new IndexWriterConfig(LUCENE_VERSION, new StandardAnalyzer());
    Directory index = new MMapDirectory(new File(INDEX_PATH + documentId));
    IndexWriter indexWriter = new IndexWriter(index, config);
    for (Chapter chapterToStore : chaptersToStore) {
      indexWriter.addDocument(addFieldsToDocument(chapterToStore));
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
