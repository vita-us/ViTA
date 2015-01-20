package de.unistuttgart.vis.vita.analysis.modules.nlp;

import de.unistuttgart.vis.vita.analysis.modules.gate.OpenNLPModule;
import de.unistuttgart.vis.vita.analysis.results.OpenNLPResult;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Set;

import gate.Annotation;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.doubleThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class OpenNLPModuleTest extends NLPTestSetup {
  private static OpenNLPModule module;
  private static OpenNLPResult openNLPResult;

  @BeforeClass
  public static void setUp() throws Exception {
    NLPTestSetup.setUp();
    module = new OpenNLPModule();
    openNLPResult = module.execute(resultProvider, progressListener);
  }

  @Test
  public void testEntityAnnotations() throws Exception {
    Set<Annotation> annotations = openNLPResult.getAnnotationsForChapter(chapterObjects.get(1));
    assertThat(annotations, is(not(empty())));
    assertThat(annotations, is(hasSize(2)));

    for (Annotation annotation : annotations) {
      assertThat(annotation, is(notNullValue()));
      assertThat(annotation.getType(), isOneOf("Location", "Person"));
    }
  }

  @Test
  public void testProgressIsReported() throws Exception {
    // Check that the 0%-100% range is covered approximately
    // This does not test smoothness as the call times are not considered.
    int steps = 5;
    for (int i = 0; i < steps; i++) {
      verify(progressListener, atLeastOnce()).observeProgress(doubleThat(
          closeTo((double) i / steps, (double) 1 / steps)));
    }
  }
}
