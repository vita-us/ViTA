package de.unistuttgart.vis.vita.analysis.modules;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.importer.txt.output.TextImportResult;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentMetadata;
import de.unistuttgart.vis.vita.model.document.DocumentPart;

public class TextFeatureModuleTest {
  private static final String PART_1_TITLE = "Part 1";
  private static final String CHAPTER_1_1_TITLE = "Chapter 1.1";
  private static final String DOCUMENT_TITLE = "The Book";
  private static final String DOCUMENT_AUTHOR = "The Famous Author";
  
  private Document document;
  private TextFeatureModule module;
  private EntityManager em;
  private ModuleResultProvider resultProvider;
  private ProgressListener listener;
  
  @Before
  public void setUp() throws Exception {
    Model model = Model.createUnitTestModel();
    em = model.getEntityManager();
    prepareDatabase();
    ImportResult result = createImportResult();
    DocumentPersistenceContext context = mock(DocumentPersistenceContext.class);
    when(context.getDocumentId()).thenReturn(document.getId());
    
    resultProvider = mock(ModuleResultProvider.class);
    when(resultProvider.getResultFor(Model.class)).thenReturn(model);
    when(resultProvider.getResultFor(DocumentPersistenceContext.class)).thenReturn(context);
    when(resultProvider.getResultFor(ImportResult.class)).thenReturn(result);
    
    listener = mock(ProgressListener.class);
    
    module = new TextFeatureModule();
  }
  
  @Test
  public void testPartsAndChaptersArePersisted() throws Exception {
    module.execute(resultProvider, listener);
    em.refresh(document);

    assertThat(document.getContent().getParts(), hasSize(2));
    DocumentPart part1 = document.getContent().getParts().get(0);
    assertThat(part1.getTitle(), is(PART_1_TITLE));
    assertThat(part1.getChapters(), hasSize(3));
    Chapter chapter11 = part1.getChapters().get(0);
    assertThat(chapter11.getTitle(), is(CHAPTER_1_1_TITLE));
  }

  @Test
  public void testMetadataIsPersisted() throws Exception {
    module.execute(resultProvider, listener);
    em.refresh(document);

    assertThat(document.getMetadata().getAuthor(), is(DOCUMENT_AUTHOR));
    assertThat(document.getMetadata().getTitle(), is(DOCUMENT_TITLE));
  }

  @Test
  public void testProgressIsSetToReadyAfterwards() throws Exception {
    module.execute(resultProvider, listener);
    em.refresh(document);

    assertThat(document.getProgress().getTextProgress().getProgress(), is(1.0));
    assertThat(document.getProgress().getTextProgress().isReady(), is(true));
  }

  @Test
  public void testProgressIsReportedDuringExecution() {
    module.observeProgress(0.2); // should be ignored as model is not provided yet

    module.dependencyFinished(Model.class, resultProvider.getResultFor(Model.class));
    module.dependencyFinished(DocumentPersistenceContext.class,
        resultProvider.getResultFor(DocumentPersistenceContext.class));
    module.observeProgress(0.5);

    em.refresh(document);
    assertThat(document.getProgress().getTextProgress().getProgress(), is(0.5));
    assertThat(document.getProgress().getTextProgress().isReady(), is(false));
  }

  private void prepareDatabase() {
    document = new Document();
    em.getTransaction().begin();
    em.persist(document);
    em.getTransaction().commit();
  }
  
  private ImportResult createImportResult() {
    final List<DocumentPart> parts = new ArrayList<>();
    
    DocumentPart part1 = new DocumentPart();
    part1.setTitle(PART_1_TITLE);
    parts.add(part1);

    Chapter chapter11 = new Chapter();
    chapter11.setTitle(CHAPTER_1_1_TITLE);
    part1.getChapters().add(chapter11);

    Chapter chapter12 = new Chapter();
    chapter12.setTitle(CHAPTER_1_1_TITLE);
    part1.getChapters().add(chapter12);

    Chapter chapter13 = new Chapter();
    chapter13.setTitle(CHAPTER_1_1_TITLE);
    part1.getChapters().add(chapter13);
    
    DocumentPart part2 = new DocumentPart();
    part2.setTitle(PART_1_TITLE);
    parts.add(part2);
    
    DocumentMetadata metadata = new DocumentMetadata(DOCUMENT_TITLE, DOCUMENT_AUTHOR);

    return new TextImportResult(parts, metadata);
  }
}
