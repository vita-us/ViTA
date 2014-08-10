package de.unistuttgart.vis.vita.persistence;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({DocumentPersistenceTest.class, DocumentPartPersistenceTest.class,
    EntityPersistenceTest.class})
public class PersistenceTests {

}
