package de.unistuttgart.vis.vita.analysis.importer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import de.unistuttgart.vis.vita.analysis.modules.TextImportModule;

public class TextImportModuleTest {
  @Test
  public void test() throws URISyntaxException, IOException {
    // The resource file is located in src/test/resources/de/unistuttgart/vis/vita/analysis/importer
    Path testPath = Paths.get(getClass().getResource("text1.txt").toURI());
    TextImportModule module = new TextImportModule(testPath);
    // TODO add assertions
  }
}
