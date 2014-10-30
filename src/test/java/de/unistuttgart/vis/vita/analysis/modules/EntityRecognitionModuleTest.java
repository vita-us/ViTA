package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.results.AnnieNLPResult;
import de.unistuttgart.vis.vita.analysis.results.BasicEntityCollection;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.document.TextPosition;
import de.unistuttgart.vis.vita.model.document.TextSpan;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;
import de.unistuttgart.vis.vita.model.entity.EntityType;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

/**
 * Unit tests for analysis modules
 */
public class EntityRecognitionModuleTest {

  // Short story: The Cunning Fox and the Clever Stork
  private final static String[]
      CHAPTERS =
      {""};

  private List<DocumentPart> parts = new ArrayList<>();
  private ModuleResultProvider resultProvider;
  private ProgressListener progressListener;
  private List<Chapter> chapterObjects;
  private BasicEntityCollection collection;

  @Before
  public void setUp() throws Exception {
    resultProvider = mock(ModuleResultProvider.class);
    ImportResult importResult = mock(ImportResult.class);
    when(importResult.getParts()).thenReturn(parts);
    when(resultProvider.getResultFor(ImportResult.class)).thenReturn(importResult);
    progressListener = mock(ProgressListener.class, withSettings());

    loadText();
    fillText();

    ANNIEModule annieModule = new ANNIEModule();
    AnnieNLPResult annieNLPResult = annieModule.execute(resultProvider, progressListener);
    when(resultProvider.getResultFor(AnnieNLPResult.class)).thenReturn(annieNLPResult);

    EntityRecognitionModule entityRecognitionModule = new EntityRecognitionModule();
    collection = entityRecognitionModule.execute(resultProvider, progressListener);
  }

  private void loadText() {
    URL resourceUrl = getClass().getResource("LOTR_CP1.txt");
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

  private void fillText() {
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
    assertThat(person.getOccurences(), hasItem(
        new TextSpan(new TextPosition(chapterObjects.get(0), 5750),
                     new TextPosition(chapterObjects.get(0), 5759))));
    assertTrue(checkIfNameExists(person, "Frodo"));
    assertTrue(checkIfNameExists(person, "Mr. Frodo"));
  }

  @Test
  public void checkPlaceRecognition() throws Exception {
    BasicEntity person = getEntityByName("Buckland");

    assertNotNull(person);
    assertThat(person.getType(), is(EntityType.PLACE));
    assertThat(person.getOccurences(), hasItem(
        new TextSpan(new TextPosition(chapterObjects.get(0), 4103),
                     new TextPosition(chapterObjects.get(0), 4111))));
    assertTrue(checkIfNameExists(person, "Buckland"));

    //System.out.println(StringUtils.join(collection.getEntities(), "\n"));
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
