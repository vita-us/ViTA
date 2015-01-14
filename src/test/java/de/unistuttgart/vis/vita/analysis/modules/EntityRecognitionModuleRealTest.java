package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.results.AnnieDatastore;
import de.unistuttgart.vis.vita.analysis.results.AnnieNLPResult;
import de.unistuttgart.vis.vita.analysis.results.BasicEntityCollection;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.document.Range;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;
import de.unistuttgart.vis.vita.model.entity.EntityType;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.doubleThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

/**
 * Unit tests for analysis modules
 */
public class EntityRecognitionModuleRealTest {

  // Short story: The Cunning Fox and the Clever Stork
  private final static String[]
      CHAPTERS =
      {""};

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

    loadText();
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

  private static void loadText() {
    URL resourceUrl = EntityRecognitionModuleRealTest.class.getResource("LOTR_CP1.txt");
    File file = new File(resourceUrl.getFile());
    String bigString = "";

    try {
      List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()),
                                              Charset.defaultCharset());
      for (String line : lines) {
        bigString = bigString.concat(line + " ");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    CHAPTERS[0] = bigString;
  }

  private static void fillText() {
    DocumentPart part = new DocumentPart();
    parts.add(part);

    chapterObjects = new ArrayList<>();
    for (String chapterText : CHAPTERS) {
      Chapter chapter = new Chapter();
      chapter.setText(chapterText);
      chapter.setLength(chapterText.length());
      part.getChapters().add(chapter);
      chapterObjects.add(chapter);
    }
  }

  @Test
  public void checkPersonRecognition() throws Exception {
    BasicEntity person = getEntityByName("Frodo");

    assertNotNull(person);
    assertThat(person.getType(), is(EntityType.PERSON));
    assertThat(person.getOccurences(), hasItem(new Range(chapterObjects.get(0), 5750, 5759)));
    assertTrue(checkIfNameExists(person, "Frodo"));
    assertTrue(checkIfNameExists(person, "Mr. Frodo"));
  }

  @Test
  public void checkPlaceRecognition() throws Exception {
    BasicEntity person = getEntityByName("Buckland");

    assertNotNull(person);
    assertThat(person.getType(), is(EntityType.PLACE));
    assertThat(person.getOccurences(), hasItem(new Range(chapterObjects.get(0), 4103, 4111)));
    assertTrue(checkIfNameExists(person, "Buckland"));
  }

  @Test
  public void testProgressIsReported() throws Exception {
    // Check that the 0%-100% range is covered approximately
    // This does not test smoothness as the call times are not considered.
    int steps = 100;
    for (int i = 0; i < steps; i++) {
      verify(progressListener, atLeastOnce()).observeProgress(doubleThat(
          closeTo((double) i / steps, (double) 1 / steps)));
    }
  }

  private boolean checkIfNameExists(BasicEntity entity, String nameToSearch) {
    for(Attribute attribute : entity.getNameAttributes()) {
      if(attribute.getContent().equals(nameToSearch)) {
        return true;
      }
    }

    return false;
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
