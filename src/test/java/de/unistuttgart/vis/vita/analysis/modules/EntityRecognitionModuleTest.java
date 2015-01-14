package de.unistuttgart.vis.vita.analysis.modules;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.results.AnnieDatastore;
import de.unistuttgart.vis.vita.analysis.results.AnnieNLPResult;
import de.unistuttgart.vis.vita.analysis.results.BasicEntityCollection;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;

import static org.mockito.Matchers.anyString;

/**
 * Unit tests for analysis modules
 */
public class EntityRecognitionModuleTest {

  // Short story: The Cunning Fox and the Clever Stork
  private final static String[]
      CHAPTERS = {"Alice went to the party.", "Alice met Bob" };

  private static List<DocumentPart> parts = new ArrayList<>();
  private static ModuleResultProvider resultProvider;
  private static ProgressListener progressListener;
  private static List<Chapter> chapterObjects;
  private static BasicEntityCollection collection;

  @BeforeClass
  public static void setUp() throws Exception {
    resultProvider = mock(ModuleResultProvider.class);
    ImportResult importResult = mock(ImportResult.class);
    when(importResult.getParts()).thenReturn(parts);
    when(resultProvider.getResultFor(ImportResult.class)).thenReturn(importResult);
    progressListener = mock(ProgressListener.class, withSettings());

    fillText();

    GateInitializeModule initializeModule = new GateInitializeModule();
    initializeModule.execute(resultProvider, progressListener);

    DocumentPersistenceContext docId = mock(DocumentPersistenceContext.class);
    when(docId.getDocumentId()).thenReturn("");
    when(resultProvider.getResultFor(DocumentPersistenceContext.class)).thenReturn(docId);

    AnnieDatastore datastore = mock(AnnieDatastore.class);
    when(datastore.getStoredAnalysis(anyString())).thenReturn(null);
    when(resultProvider.getResultFor(AnnieDatastore.class)).thenReturn(datastore);

    ANNIEModule annieModule = new ANNIEModule();
    AnnieNLPResult annieNLPResult = annieModule.execute(resultProvider, progressListener);
    when(resultProvider.getResultFor(AnnieNLPResult.class)).thenReturn(annieNLPResult);
    when(resultProvider.getResultFor(AnnieDatastore.class)).thenReturn(datastore);

    EntityRecognitionModule entityRecognitionModule = new EntityRecognitionModule();
    collection = entityRecognitionModule.execute(resultProvider, progressListener);
  }

  private static void fillText() {
    DocumentPart part = new DocumentPart();
    parts.add(part);

    int documentLength = 0;
    for(String chapterText : CHAPTERS){
      documentLength += chapterText.length();
    }
    
    chapterObjects = new ArrayList<>();
    int pos = 0;
    for (String chapterText : CHAPTERS) {
      Chapter chapter = new Chapter();
      chapter.setText(chapterText);
      chapter.setLength(chapterText.length());
      chapter.setRange(new Range(TextPosition.fromGlobalOffset(chapter, pos, documentLength),
          TextPosition.fromGlobalOffset(chapter, pos + chapterText.length(), documentLength)));
      pos += chapterText.length();
      part.getChapters().add(chapter);
      chapterObjects.add(chapter);
    }
  }

  @Test
  public void checkEntitiesAreDetectedAcrossChapters() throws Exception {
    BasicEntity person = getEntityByName("Alice");
    assertThat(person, not(nullValue()));
    assertThat(person.getOccurences(), hasSize(2));
  }

  private BasicEntity getEntityByName(String name) {
    for(BasicEntity entity : collection.getEntities()) {
      for(Attribute attribute : entity.getNameAttributes()) {
          if(attribute.getContent().equals(name)) {
            return entity;
          }
      }
    }

    return null;
  }
}
