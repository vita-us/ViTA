package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.analysis.results.TextMetrics;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.progress.AnalysisProgress;
import de.unistuttgart.vis.vita.model.progress.FeatureProgress;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

import javax.persistence.EntityManager;

/**
 * The feature module that stores document outline and document metadata
 *
 * "Feature module" means that it interacts with the data base. It stores the progress as well as
 * the result.
 *
 * This module depends on {@link IndexSearcher} which guarantees that the document text is persisted
 * in lucene.
 */
@AnalysisModule(dependencies = {ImportResult.class, DocumentPersistenceContext.class, Model.class,
                                TextMetrics.class}, weight = 0.1)
public class TextFeatureModule extends AbstractFeatureModule<TextFeatureModule> {

  @Override
  public TextFeatureModule storeResults(ModuleResultProvider result, Document document,
      EntityManager em, ProgressListener progressListener)
      throws Exception {
    
    ImportResult importResult = result.getResultFor(ImportResult.class);
    TextMetrics textMetrics = result.getResultFor(TextMetrics.class);

    addDocumentData(document, importResult);
    updateDocumentMetrics(document, importResult, textMetrics);
    persistPartsAndChapters(importResult, em);
    updateDocumentMetadata(document, importResult);
    
    return this;
  }

  @Override
  protected Iterable<FeatureProgress> getProgresses(AnalysisProgress progress) {
    return Arrays.asList(progress.getTextProgress());
  }

  /**
   * Adds data (for example: parts) to the Document.
   * 
   * @param document - The Document the analysis belongs to.
   * @param importResult - The result of the import for this document.
   */
  private void addDocumentData(Document document, ImportResult importResult){
    document.getContent().getParts().addAll(importResult.getParts());
  }

  /**
   * Updates the Metrics (character/chapter/word count) of this Document.
   * 
   * @param document - The Document the analysis belongs to.
   * @param importResult - The result of the import for this Document.
   * @param textMetrics - The result of the text analysis.
   */
  private void updateDocumentMetrics(Document document, ImportResult importResult, TextMetrics textMetrics){
    int characterCount = 0;
    int chapterCount = 0;
    for (DocumentPart part : importResult.getParts()) {
      for (Chapter chapter : part.getChapters()) {
        chapterCount++;
        characterCount += chapter.getLength();
      }
    }
    document.getMetrics().setCharacterCount(characterCount);
    document.getMetrics().setChapterCount(chapterCount);
    document.getMetrics().setWordCount(textMetrics.getWordCount());
  }
  
  /**
   * Persists the parts and chapters of the Document.
   * 
   * @param importResult - The result of the import for this Document.
   * @param em - The Entity Manager.
   */
  private void persistPartsAndChapters(ImportResult importResult, EntityManager em){
    for (DocumentPart part : importResult.getParts()) {
      em.persist(part);
      for (Chapter chapter : part.getChapters()) {
        em.persist(chapter);
      }
    }
  }
  
  /**
   * Updates the metadata (title/author/...) of this document.
   * 
   * @param document - The Document the analysis belongs to.
   * @param importResult - The result of the import for this Document.
   */
  private void updateDocumentMetadata(Document document, ImportResult importResult){
    String oldTitle = document.getMetadata().getTitle();
    document.setMetadata(importResult.getMetadata());

    // Restore the old title which is the file name if no title has been found
    if (StringUtils.isEmpty(document.getMetadata().getTitle())) {
      document.getMetadata().setTitle(oldTitle);
    }
  }
  
  
}
