/*
 * NLPTestSetup.java
 *
 */

package de.unistuttgart.vis.vita.analysis.modules.gate;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.modules.gate.GateInitializeModule;
import de.unistuttgart.vis.vita.analysis.results.AnnieDatastore;
import de.unistuttgart.vis.vita.analysis.results.DocumentPersistenceContext;
import de.unistuttgart.vis.vita.analysis.results.ImportResult;
import de.unistuttgart.vis.vita.model.document.AnalysisParameters;
import de.unistuttgart.vis.vita.model.document.Chapter;
import de.unistuttgart.vis.vita.model.document.Document;
import de.unistuttgart.vis.vita.model.document.DocumentPart;
import de.unistuttgart.vis.vita.model.document.EnumNLP;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

/**
 *
 */
public class NLPTestSetup {

  private final static String[] CHAPTERS = {
      "The text of the first chapter which consists of two sentences. The second one is short.",
      "Dave went to New York this summer.",
      "Just some more chapters ...",
      "... to properly test the progress feature ...",
      "... because the granularity can not be higher than the number of chapters."
  };

  protected static List<DocumentPart> parts = new ArrayList<>();
  protected static ModuleResultProvider resultProvider;
  protected static ProgressListener progressListener;
  protected static List<Chapter> chapterObjects;

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

  public static void setUp() throws Exception {
    ImportResult importResult = mock(ImportResult.class);
    when(importResult.getParts()).thenReturn(parts);
    GateInitializeModule initializeModule = new GateInitializeModule();
    initializeModule.execute(resultProvider, progressListener);
    DocumentPersistenceContext testingID = new DocumentPersistenceContext() {
      @Override
      public Document getDocument() {
        return null;
      }

      @Override
      public String getDocumentId() {
        return "testID123";
      }

      @Override
      public String getDocumentContentId() {
        return "testID123";
      }

      @Override
      public String getFileName() {
        return "testID123";
      }
    };
    AnnieDatastore datastore = mock(AnnieDatastore.class);
    when(datastore.getStoredAnalysis(anyString())).thenReturn(null);
    AnalysisParameters parameters = mock(AnalysisParameters.class);
    when(parameters.getNlpTool()).thenReturn(EnumNLP.ANNIE);

    resultProvider = mock(ModuleResultProvider.class);
    when(resultProvider.getResultFor(ImportResult.class)).thenReturn(importResult);
    when(resultProvider.getResultFor(AnnieDatastore.class)).thenReturn(datastore);
    when(resultProvider.getResultFor(DocumentPersistenceContext.class)).thenReturn(testingID);
    when(resultProvider.getResultFor(AnalysisParameters.class)).thenReturn(parameters);

    progressListener = mock(ProgressListener.class, withSettings());
    fillText();
  }
}
