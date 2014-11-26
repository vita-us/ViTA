package de.unistuttgart.vis.vita.model;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * An implementation of {@link DirectoryFactory} that stores the indices in a test directory
 */
public class UnitTestDirectoryFactory implements DirectoryFactory {
  public static final String RELATIVE_DIRECTORY_PATH = ".vita/test/lucene";

  public static Path getRootPath() {
    return Paths.get(System.getProperty("user.home")).resolve(RELATIVE_DIRECTORY_PATH);
  }

  @Override
  public Directory getDirectory(String name) throws IOException {
    Path path = getRootPath().resolve(name);
    return FSDirectory.open(path.toFile());
  }

}
