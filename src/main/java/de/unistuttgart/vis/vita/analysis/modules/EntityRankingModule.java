package de.unistuttgart.vis.vita.analysis.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import de.unistuttgart.vis.vita.analysis.Module;
import de.unistuttgart.vis.vita.analysis.ModuleResultProvider;
import de.unistuttgart.vis.vita.analysis.ProgressListener;
import de.unistuttgart.vis.vita.analysis.annotations.AnalysisModule;
import de.unistuttgart.vis.vita.analysis.results.BasicEntityCollection;
import de.unistuttgart.vis.vita.analysis.results.EntityRanking;
import de.unistuttgart.vis.vita.model.entity.BasicEntity;

@AnalysisModule(dependencies = {BasicEntityCollection.class}, weight = 0.1)
public class EntityRankingModule extends Module<EntityRanking> {
  @Override
  public EntityRanking execute(ModuleResultProvider results, ProgressListener progressListener)
      throws Exception {
    BasicEntityCollection entities = results.getResultFor(BasicEntityCollection.class);
    
    final List<BasicEntity> result = Lists.newArrayList(entities.getEntities());
    Collections.sort(result, new EntityComparator());
    
    return new EntityRanking() {
      @Override
      public List<BasicEntity> getRankedEntities() {
        return result;
      }
    };
  }
  
  /**
   * Compares two entities according to their occurrance count, in descending order
   */
  private static class EntityComparator implements Comparator<BasicEntity> {

    @Override
    public int compare(BasicEntity o1, BasicEntity o2) {
      if (o1 == null && o2 == null)
        return 0;
      if (o1 == null)
        return 1;
      if (o2 == null)
        return -1;
      return Integer.compare(o2.getOccurences().size(), o1.getOccurences().size());
    }
    
  }

}
