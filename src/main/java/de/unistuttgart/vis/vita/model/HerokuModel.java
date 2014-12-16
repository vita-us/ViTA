package de.unistuttgart.vis.vita.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Persistence;

public class HerokuModel extends Model {
  private static final String HEROKU_PERSISTENCE_UNIT_NAME = "de.unistuttgart.vis.vita.heroku";
  
  public HerokuModel() {
    super(Persistence.createEntityManagerFactory(HEROKU_PERSISTENCE_UNIT_NAME,
        getProperties()), new TextRepository(new DefaultDirectoryFactory()));
    // TODO: the text repository will not be shared like this
  }

  private static Map<String, String> getProperties() {
    URI dbUri;
    try {
      dbUri = new URI(System.getenv("DATABASE_URL"));
    } catch (URISyntaxException e) {
      throw new RuntimeException("Environment variable DATABASE_URL is not a url: "
          + e.getMessage(), e);
    }
    String userName = dbUri.getUserInfo().split(":")[0];
    String password = dbUri.getUserInfo().split(":")[1];
    String dbUrl = "jdbc:" + dbUri.getScheme() + "://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

    Map<String, String> properties = new HashMap<String, String>();
    properties.put("javax.persistence.jdbc.url", dbUrl);
    properties.put("javax.persistence.jdbc.user", userName);
    properties.put("javax.persistence.jdbc.password", password);
    return properties;
  }
}
