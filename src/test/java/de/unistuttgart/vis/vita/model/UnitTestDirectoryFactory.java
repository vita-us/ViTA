package de.unistuttgart.vis.vita.model;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * An implementation of {@link DirectoryFactory} that stores the indices in a random temp directory
 */
public class UnitTestDirectoryFactory implements DirectoryFactory {
  public static final String RELATIVE_DIRECTORY_PATH = ".vita/test/lucene";
  
  private Path rootPath;
  
  public UnitTestDirectoryFactory() {
    rootPath = Paths.get(System.getProperty("user.home")).resolve(RELATIVE_DIRECTORY_PATH)
        .resolve(UUID.randomUUID().toString());
  }
  
  public void remove() {
    try {
      FileUtils.deleteDirectory(rootPath.toFile());
    } catch (IOException e) {
      throw new RuntimeException("Error cleaning up lucene index directory", e);
    }
  }

  @Override
  public Directory getDirectory(String name) throws IOException {
    Path path = rootPath.resolve(name);
    return FSDirectory.open(path.toFile());
  }

}
