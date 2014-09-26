package de.unistuttgart.vis.vita.services;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.unistuttgart.vis.vita.services.occurrence.EntityOccurrencesServiceTest;

/**
 * A suite containing the tests for the REST API
 */
@RunWith(Suite.class)
@SuiteClasses({VersionServiceTest.class, DocumentsServiceTest.class, DocumentServiceTest.class,
    ChapterServiceTest.class, ProgressServiceTest.class, PersonsServiceTest.class, 
    PersonServiceTest.class, PlacesServiceTest.class, PlaceServiceTest.class, 
    DocumentPartsServiceTest.class, AnalysisServiceTest.class, EntityRelationsServiceTest.class,
    EntityOccurrencesServiceTest.class})
public class ServiceTests {

}
