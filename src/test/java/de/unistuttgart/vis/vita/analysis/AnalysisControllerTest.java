package de.unistuttgart.vis.vita.analysis;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;

import de.unistuttgart.vis.vita.RandomBlockJUnit4ClassRunner;
import de.unistuttgart.vis.vita.model.Model;
import de.unistuttgart.vis.vita.model.UnitTestModel;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;
import de.unistuttgart.vis.vita.model.document.Document;

@RunWith(RandomBlockJUnit4ClassRunner.class)
public class AnalysisControllerTest {
  private Model model;
  private AnalysisController controller;
  private AnalysisExecutorFactory executorFactory;
  private AnalysisExecutor executor;
  private AnalysisObserver analysisObserver;

  @Before
  public void setUp() throws Exception {
    UnitTestModel.startNewSession();
    model = new UnitTestModel();
    executorFactory = mock(AnalysisExecutorFactory.class);
    controller = new AnalysisController(model, executorFactory);
    removeAllDocuments();
  }

  private void removeAllDocuments() {
    EntityManager em = model.getEntityManager();
    em.getTransaction().begin();
    List<Document> documents =
        em.createNamedQuery("Document.findAllDocuments", Document.class).getResultList();
    for (Document document : documents) {
      em.remove(document);
    }
    em.getTransaction().commit();
    em.close();
  }

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testScheduleDocumentAnalysis() {
    Path path = Paths.get("path/to/file.name");
    prepareExecutor(path);
    String id = controller.scheduleDocumentAnalysis(path, "file.name");
    verifyExecutorCreated(id, path);

    assertThat(controller.documentsInQueue(), is(0)); // nothing queued, just one working
    assertThat(controller.isWorking(), is(true));

    Document document = getSingleDocument();
    assertThat(document.getId(), is(id));
    assertThat(document.getMetadata().getTitle(), is("file.name"));

    analysisObserver.onFinish(executor);

    assertThat(controller.documentsInQueue(), is(0));
    assertThat(controller.isWorking(), is(false));
  }

  private Document getSingleDocument() {
    EntityManager em = model.getEntityManager();
    List<Document> documents =
        em.createQuery("from Document", Document.class).getResultList();
    assertThat(documents, hasSize(1));
    em.close();
    return documents.get(0);
  }

  @Test
  public void testFurtherDocumentAreQueued() {
    Path path1 = Paths.get("path/to/file.name");
    Path path2 = Paths.get("path/to/file2.name");
    prepareExecutor(path1);

    controller.scheduleDocumentAnalysis(path1, "file.name");
    controller.scheduleDocumentAnalysis(path2, "file2.name");

    assertThat(controller.documentsInQueue(), is(1));
  }

  @Test
  public void testCancelAnalysis() {
    Path path = Paths.get("correctFilePath");
    prepareExecutor(path);

    String documentId = controller.scheduleDocumentAnalysis(path, "correctFilePath");

    assertThat(controller.isWorking(), is(true));

    controller.cancelAnalysis(documentId);

    assertThat(controller.isWorking(), is(false));
  }

  @Test
  public void testCancelScheduledAnalysis() {
    Path path1 = Paths.get("correctFilePath");
    Path path2 = Paths.get("correctFilePath2");
    prepareExecutor(path1);
    controller.scheduleDocumentAnalysis(path1, "correctFilePath1");
    prepareExecutor(path2);
    String id2 = controller.scheduleDocumentAnalysis(path2, "correctFilePath2");

    assertThat(controller.documentsInQueue(), is(1));

    controller.cancelAnalysis(id2);

    assertThat(controller.documentsInQueue(), is(0));
  }

  @Test
  public void testCancelUnknownDocumentIsIgnored() {
    Path path1 = Paths.get("correctFilePath");
    Path path2 = Paths.get("correctFilePath2");
    prepareExecutor(path1);
    controller.scheduleDocumentAnalysis(path1, "correctFilePath1");
    prepareExecutor(path2);
    controller.scheduleDocumentAnalysis(path2, "correctFilePath2");

    assertThat(controller.documentsInQueue(), is(1));

    controller.cancelAnalysis("some-unkonw-id");

    assertThat(controller.documentsInQueue(), is(1));
  }

  @Test
  public void testStartsQueuedAnalysisWhenFirstOneFinishes() {
    Path path1 = Paths.get("path/to/file.name");
    Path path2 = Paths.get("path/to/file2.name");
    prepareExecutor(path1);

    String id1 = controller.scheduleDocumentAnalysis(path1, "file.name");
    String id2 = controller.scheduleDocumentAnalysis(path2, "file2.name");

    verifyExecutorCreated(id1, path1);
    verify(executor).start();
    assertThat(controller.documentsInQueue(), is(1));
    assertThat(controller.isWorking(), is(true));

    AnalysisExecutor executor1 = executor;
    prepareExecutor(path2);
    analysisObserver.onFinish(executor1);

    verifyExecutorCreated(id2, path2);
    verify(executor).start();
    assertThat(controller.documentsInQueue(), is(0));
    assertThat(controller.isWorking(), is(true));
  }

  @Test
  public void testStartsQueuedAnalysisWhenFirstOneFails() {
    Path path1 = Paths.get("path/to/file.name");
    Path path2 = Paths.get("path/to/file2.name");
    prepareExecutor(path1);
    String id1 = controller.scheduleDocumentAnalysis(path1, "file.name");
    String id2 = controller.scheduleDocumentAnalysis(path2, "file2.name");
    verifyExecutorCreated(id1, path1);
    AnalysisExecutor executor1 = executor;
    prepareExecutor(path2);
    analysisObserver.onFail(executor1);

    verifyExecutorCreated(id2, path2);
    verify(executor).start();
    assertThat(controller.documentsInQueue(), is(0));
    assertThat(controller.isWorking(), is(true));
  }

  @Test
  public void testRestartDocumentAnalysis() {
    // We need a realy database backend here
    controller = new AnalysisController(model, executorFactory);

    Path path = Paths.get("path/to/file.name");
    prepareExecutor(path);
    AnalysisParameters params = new AnalysisParameters();
    String id = controller.scheduleDocumentAnalysis(path, "file.name", params);
    verifyExecutorCreated(id, path);

    controller.cancelAnalysis(id);

    prepareExecutor(path);
    controller.restartAnalysis(id);

    // Make sure it has been called the second time
    verify(executorFactory, times(2)).createExecutor(id, path, params);
  }

  private void prepareExecutor(Path path) {
    executor = mock(AnalysisExecutor.class);
    when(executorFactory.createExecutor(anyString(), eq(path),
        Matchers.any(AnalysisParameters.class)))
        .thenReturn(executor);
  }

  private void verifyExecutorCreated(String id, Path path) {
    verify(executorFactory).createExecutor(eq(id), eq(path), Matchers.any(AnalysisParameters.class));
    ArgumentCaptor<AnalysisObserver> observerCaptor;
    observerCaptor = ArgumentCaptor.forClass(AnalysisObserver.class);
    verify(executor).addObserver(observerCaptor.capture());
    analysisObserver = observerCaptor.getValue();
  }

}
