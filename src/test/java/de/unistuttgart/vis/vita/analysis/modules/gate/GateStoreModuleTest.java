package de.unistuttgart.vis.vita.analysis.modules.gate;

import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.results.AnnieDatastore;
import de.unistuttgart.vis.vita.model.GateDatastoreLocation;
import de.unistuttgart.vis.vita.model.Model;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.List;

import gate.Corpus;
import gate.DataStore;
import gate.corpora.CorpusImpl;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GateStoreModuleTest {

  private static final String TESTID = "test123";
  private static DataStore dataStore;
  private static GateStoreModule module;
  private static ModuleResultProvider resultProvider;
  private static ProgressListener progressListener;
  private static AnnieDatastore storeModule;

  @BeforeClass
  public static void setUp() throws Exception {
    resultProvider = mock(ModuleResultProvider.class);
    progressListener = mock(ProgressListener.class);

    GateInitializeModule initializeModule = new GateInitializeModule();
    initializeModule.execute(resultProvider, progressListener);

    module = new GateStoreModule();
    String userDir = System.getProperty("user.dir");
    File testDir = new File(userDir + File.separator + "testDS");

    GateDatastoreLocation location = mock(GateDatastoreLocation.class);
    when(location.getLocation()).thenReturn(testDir.toURI());

    Model model = mock(Model.class);
    when(model.getGateDatastoreLocation()).thenReturn(location);
    when(resultProvider.getResultFor(Model.class)).thenReturn(model);

    storeModule = module.execute(resultProvider, progressListener);
    dataStore = storeModule.getDatastore();
  }

  @AfterClass
  public static void tearDown() throws Exception {
    dataStore.delete();
  }

  @Test
  public void testStoringResult() throws Exception {
    Corpus test = new CorpusImpl();

    assertThat(dataStore.getLrTypes(), is(empty()));

    storeModule.storeResult(test, TESTID);

    List<String> lrIds = dataStore.getLrIds("gate.corpora.SerialCorpusImpl");
    String testResourceId = lrIds.get(0);

    assertThat(lrIds, is(not(empty())));
    assertThat(testResourceId, is(TESTID));

    dataStore.delete("gate.corpora.SerialCorpusImpl", TESTID);
  }

  @Test
  public void testRemovingResult() throws Exception {
    Corpus test = new CorpusImpl();
    storeModule.storeResult(test, TESTID);

    assertThat(dataStore.getLrTypes(), is(not(empty())));

    storeModule.removeResult(TESTID);

    assertThat(dataStore.getLrTypes(), is(empty()));
  }
}
