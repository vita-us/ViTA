package de.unistuttgart.vis.vita.analysis.modules.gate;

import de.unistuttgart.vis.vita.analysis.results.AnnieNLPResult;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Set;

import gate.Annotation;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.doubleThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class ANNIEModuleTest extends NLPTestSetup {

  private static ANNIEModule module;

  @BeforeClass
  public static void setUp() throws Exception {
    NLPTestSetup.setUp();
    module = new ANNIEModule();
  }

  @Test
  public void testEntityAnnotations() throws Exception {
    AnnieNLPResult result = module.execute(resultProvider, progressListener);

    Set<Annotation> annotations = result.getAnnotationsForChapter(chapterObjects.get(1));
    assertThat(annotations, is(not(empty())));
    assertThat(annotations, is(hasSize(2)));

    boolean nameExists = false;

    for (Annotation i : annotations) {
      if (i.getFeatures().get("firstName") != null) {
        nameExists = true;
      }
    }

    assertThat(nameExists, is(true));
  }

  @Test
  public void testProgressIsReported() throws Exception {
    module.execute(resultProvider, progressListener);

    // Check that the 0%-100% range is covered approximately
    // This does not test smoothness as the call times are not considered.
    int steps = 5;
    for (int i = 0; i < steps; i++) {
      verify(progressListener, atLeastOnce()).observeProgress(doubleThat(
          closeTo((double) i / steps, (double) 1 / steps)));
    }
  }
}
