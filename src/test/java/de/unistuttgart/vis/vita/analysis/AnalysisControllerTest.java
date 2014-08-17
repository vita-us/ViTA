package de.unistuttgart.vis.vita.analysis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unistuttgart.vis.vita.model.Model;

public class AnalysisControllerTest {

  private AnalysisController controller;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @Before
  public void setUp() throws Exception {
    de.unistuttgart.vis.vita.model.Model testModel = new Model();
    controller = new AnalysisController(testModel);
  }

  @After
  public void tearDown() throws Exception {}
  
  @Test
  public void testScheduleDocumentAnalysis() {
    controller.scheduleDocumentAnalysis(Paths.get("correctFilePath"));
    assertThat(controller.documentsInQueue(), is(1));
  }
  
  @Test
  public void testStartAnalysis() {
    controller.scheduleDocumentAnalysis(Paths.get("correctFilePath"));
    assertThat(controller.isWorking(), is(true));
  }
  
  @Test
  public void testCancelAnalysis() {
    String documentId = controller.scheduleDocumentAnalysis(Paths.get("correctFilePath"));
    assertThat(controller.isWorking(), is(true));
    controller.cancelAnalysis(documentId);
    assertThat(controller.isWorking(), is(false));
  }
  
  @Test
  public void testRestartAnalysis() {
    fail("Not yet implemented");
  }

}
