package de.unistuttgart.vis.vita.analysis.modules;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.results.AnnieDatastore;
import de.unistuttgart.vis.vita.analysis.results.AnnieNLPResult;
import de.unistuttgart.vis.vita.analysis.results.BasicEntityCollection;
import de.unistuttgart.vis.vita.analysis.results.EntityAttributes;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.entity.Attribute;
import de.unistuttgart.vis.vita.model.entity.AttributeType;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class EntityAttributeModuleTest {

  private final static String[]
      CHAPTERS =
      {""};

  private static List<DocumentPart> parts = new ArrayList<>();
  private static BasicEntityCollection collection;
  private static EntityAttributes entityAttributes;

  @BeforeClass
  public static void setUp() throws Exception {
    ModuleResultProvider resultProvider = mock(ModuleResultProvider.class);
    ImportResult importResult = mock(ImportResult.class);
    when(importResult.getParts()).thenReturn(parts);
    when(resultProvider.getResultFor(ImportResult.class)).thenReturn(importResult);
    ProgressListener progressListener = mock(ProgressListener.class, withSettings());

    loadText();
    fillText();

    GateInitializeModule initializeModule = new GateInitializeModule();
    initializeModule.execute(resultProvider, progressListener);

    AnnieDatastore datastore = mock(AnnieDatastore.class);
    when(datastore.getStoredAnalysis(anyString())).thenReturn(null);

    ANNIEModule annieModule = new ANNIEModule();
    AnnieNLPResult annieNLPResult = annieModule.execute(resultProvider, progressListener);
    when(resultProvider.getResultFor(AnnieNLPResult.class)).thenReturn(annieNLPResult);
    when(resultProvider.getResultFor(AnnieDatastore.class)).thenReturn(datastore);

    EntityRecognitionModule entityRecognitionModule = new EntityRecognitionModule();
    collection = entityRecognitionModule.execute(resultProvider, progressListener);
    when(resultProvider.getResultFor(BasicEntityCollection.class)).thenReturn(collection);

    EntityAttributeModule entityAttributeModule = new EntityAttributeModule();
    entityAttributes = entityAttributeModule.execute(resultProvider, progressListener);
  }

  private static void loadText() throws URISyntaxException, IOException {
    URL resourceUrl = EntityAttributeModuleTest.class.getResource("LOTR_CP1.txt");
    File file = new File(resourceUrl.toURI());
    String bigString = "";

    List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()),
                                            Charset.defaultCharset());
    for (String line : lines) {
      bigString = bigString.concat(line + " ");
    }
    CHAPTERS[0] = bigString;
  }

  private static void fillText() {
    DocumentPart part = new DocumentPart();
    parts.add(part);

    List<Chapter> chapterObjects = new ArrayList<>();

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
    BasicEntity femaleEntity = getEntityByName("Dora");
    BasicEntity maleEntity = getEntityByName("Frodo");

    assertThat(entityAttributes.getAttributesForEntity(femaleEntity), is(not(empty())));
    assertThat(entityAttributes.getAttributesForEntity(maleEntity), is(not(empty())));

    Attribute genderForFemale = getGenderAttribute(femaleEntity);
    Attribute genderForMale = getGenderAttribute(maleEntity);

    assertThat(genderForFemale, is(not(nullValue())));
    assertThat(genderForMale, is(not(nullValue())));

    assertThat(genderForFemale.getContent(), is("female"));
    assertThat(genderForMale.getContent(), is("male"));
  }

  private BasicEntity getEntityByName(String name) {
    for (BasicEntity entity : collection.getEntities()) {
      for (Attribute attribute : entity.getNameAttributes()) {
        if (attribute.getContent().equals(name)) {
          return entity;
        }
      }
    }

    return null;
  }

  private Attribute getGenderAttribute(BasicEntity entity) {
    Set<Attribute> attributesForEntity = entityAttributes.getAttributesForEntity(entity);

    for (Attribute attribute : attributesForEntity) {
      if (attribute.getType() == AttributeType.GENDER) {
        return attribute;
      }
    }

    return null;
  }
}
