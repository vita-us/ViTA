package de.unistuttgart.vis.vita.analysis;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.nio.file.Paths;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.document.Document;

public class AnalysisControllerTest {
  private Model model;
  private EntityManager entityManager;
  private AnalysisController controller;

  @Before
  public void setUp() throws Exception {
    model = mock(Model.class);
    entityManager = mock(EntityManager.class);
    when(model.getEntityManager()).thenReturn(entityManager);
    controller = new AnalysisController(model);
  }

  @After
  public void tearDown() throws Exception {}
  
  @Test
  public void testScheduleDocumentAnalysis() {
    ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
    
    String id = controller.scheduleDocumentAnalysis(Paths.get("path/to/file.name"));

    assertThat(controller.documentsInQueue(), is(0)); // nothing queued, just one working
    assertThat(controller.isWorking(), is(true));
    verify(entityManager).persist(documentCaptor.capture());
    Document document = documentCaptor.getValue();
    assertThat(document.getId(), is(id));
    assertThat(document.getMetadata().getTitle(), is("file.name"));
  }
  
  @Test
  public void testFurtherDocumentAreQueued() {
    controller.scheduleDocumentAnalysis(Paths.get("path/to/file.name"));
    controller.scheduleDocumentAnalysis(Paths.get("path/to/file2.name"));

    assertThat(controller.documentsInQueue(), is(1));
  }
  
  @Test
  public void testCancelAnalysis() {
    String documentId = controller.scheduleDocumentAnalysis(Paths.get("correctFilePath"));
    assertThat(controller.isWorking(), is(true));
    controller.cancelAnalysis(documentId);
    assertThat(controller.isWorking(), is(false));
  }

}
