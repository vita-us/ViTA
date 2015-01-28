/*
 *  Copyright (c) 2006-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  $Id: DependencyMode.java 15609 2012-03-21 11:00:31Z adamfunk $
 */
package gate.stanford;

import java.util.Collection;

import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

public enum DependencyMode {
  Typed,
  AllTyped,
  TypedCollapsed,
  TypedCCprocessed;
  
  
  protected static Collection<TypedDependency> getDependencies(GrammaticalStructure gs,
      DependencyMode mode, boolean includeExtras) {
    Collection<TypedDependency> result = null;
    
    if (mode.equals(Typed)) {
      result = gs.typedDependencies(includeExtras);
    }
    else if (mode.equals(AllTyped)) {
      result = gs.allTypedDependencies();
    }
    else if (mode.equals(TypedCollapsed)) {
      result = gs.typedDependenciesCollapsed(includeExtras);
    }
    else if (mode.equals(TypedCCprocessed)) {
      result = gs.typedDependenciesCCprocessed(includeExtras);
    }
    
    return result;
  }

}
