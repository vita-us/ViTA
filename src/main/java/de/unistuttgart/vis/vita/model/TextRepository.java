package de.unistuttgart.vis.vita.model;

import de.unistuttgart.vis.vita.model.document.Chapter;

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
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages texts for the model.
 */
public class TextRepository {

  private static final String CHAPTER_ID = "chapterId";
  private static final String CHAPTER_TEXT = "chapterText";
  private static final Version LUCENE_VERSION = Version.LUCENE_4_10_0;
  List<Document> docs = new ArrayList<>();
  private DirectoryFactory directoryFactory;
  // list of directories
  private List<Directory> indexes = new ArrayList<>();

  /**
   * Creates a TextRepository with the default DirectoryFactory
   */
  public TextRepository() {
    directoryFactory = new DefaultDirectoryFactory();
  }

  /**
   * Creates a TextRepository with a custom DirectoryFactory
   *
   * @param directoryFactory the factory that should be used to instantiate lucene directories
   */
  public TextRepository(DirectoryFactory directoryFactory) {
    this.directoryFactory = directoryFactory;
  }

  /**
   * Sets the text of the commited chapter with the related chapter text of lucene index
   */
  public void populateChapterText(Chapter chapterToPopulate, String documentId) throws IOException,
                                                                                       ParseException {
    Directory directory = directoryFactory.getDirectory(documentId);
    IndexReader indexReader = DirectoryReader.open(directory);
    IndexSearcher indexSearcher = new IndexSearcher(indexReader);
    QueryParser queryParser = new QueryParser(CHAPTER_ID, new StandardAnalyzer());
    Query query = queryParser.parse(chapterToPopulate.getId());
    ScoreDoc[] hits = indexSearcher.search(query, 1).scoreDocs;
    Document hitDoc = indexSearcher.doc(hits[0].doc);
    chapterToPopulate.setText(hitDoc.getField(CHAPTER_TEXT).stringValue());
  }

  /**
   * Stores a list of chapters of an ebook in a lucene directory
   */
  public void storeChaptersTexts(List<Chapter> chaptersToStore, String documentId)
      throws IOException {

    IndexWriterConfig config = new IndexWriterConfig(LUCENE_VERSION, new StandardAnalyzer());
    Directory directory = directoryFactory.getDirectory(documentId);
    IndexWriter indexWriter = new IndexWriter(directory, config);
    for (Chapter chapterToStore : chaptersToStore) {
      indexWriter.addDocument(addFieldsToDocument(chapterToStore));
    }

    // at the created index along with its documents to the indexes list
    indexes.add(directory);
    indexWriter.close();
  }

  /**
   * Returns the related IndexSearcher regarding this document
   */
  public IndexSearcher getIndexSearcherForDocument(String documentId) throws IOException {
    Directory directory = directoryFactory.getDirectory(documentId);
    IndexReader indexReader = DirectoryReader.open(directory);
    return new IndexSearcher(indexReader);
  }

  /**
   * @return the indexes
   */
  public List<Directory> getIndexes() {
    return indexes;
  }

  /**
   * Creates a document and adds the chapter id and chapter text field to this
   */
  private Document addFieldsToDocument(Chapter chapterToStore) {
    Document document = new Document();
    document.add(new Field(CHAPTER_ID, chapterToStore.getId(), TextField.TYPE_STORED));
    document.add(new Field(CHAPTER_TEXT, chapterToStore.getText(), TextField.TYPE_STORED));
    docs.add(document);
    return document;
  }
}
