package de.unistuttgart.vis.vita.persistence;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({AttributePersistenceTest.class, ChapterPersistenceTest.class,
    DocumentPersistenceTest.class, DocumentPartPersistenceTest.class, EntityPersistenceTests.class,
    EntityRelationPersistenceTest.class, OccurrencePersistenceTest.class, AbstractEntityBaseTest.class, 
    AnalysisProgressPersistenceTest.class})
public class PersistenceTests {

}
