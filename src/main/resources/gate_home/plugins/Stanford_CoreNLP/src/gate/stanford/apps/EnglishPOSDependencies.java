package gate.stanford.apps;

import gate.creole.PackagedController;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;

import java.net.URL;
import java.util.List;

@CreoleResource(name="English POS Tagger and Dependency Parser", autoinstances = @AutoInstance)
public class EnglishPOSDependencies extends PackagedController {

  private static final long serialVersionUID = 3163023140886167369L;

  @Override
  @CreoleParameter(defaultValue="sample_pos+parser_en.gapp")
  public void setPipelineURL(URL url) {
    this.url = url;    
  }
  
  @Override
  @CreoleParameter(defaultValue="Stanford Parser")
  public void setMenu(List<String> menu) {
    super.setMenu(menu);
  }
}
