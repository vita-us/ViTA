package de.unistuttgart.vis.vita.services;

import de.unistuttgart.vis.vita.services.analysis.ParametersServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.unistuttgart.vis.vita.services.analysis.AnalysisServiceTest;
import de.unistuttgart.vis.vita.services.analysis.ProgressServiceTest;
import de.unistuttgart.vis.vita.services.document.ChapterServiceTest;
import de.unistuttgart.vis.vita.services.document.DocumentPartsServiceTest;
import de.unistuttgart.vis.vita.services.document.DocumentServiceTest;
import de.unistuttgart.vis.vita.services.document.DocumentsServiceTest;
import de.unistuttgart.vis.vita.services.entity.AttributeServiceTest;
import de.unistuttgart.vis.vita.services.entity.AttributesServiceTest;
import de.unistuttgart.vis.vita.services.entity.EntityRelationsServiceTest;
import de.unistuttgart.vis.vita.services.entity.PersonServiceTest;
import de.unistuttgart.vis.vita.services.entity.PersonsServiceTest;
import de.unistuttgart.vis.vita.services.entity.PlaceServiceTest;
import de.unistuttgart.vis.vita.services.entity.PlacesServiceTest;
import de.unistuttgart.vis.vita.services.occurrence.OccurrencesServiceTests;

/**
 * A suite containing the tests for the REST API
 */
@RunWith(Suite.class)
@SuiteClasses({VersionServiceTest.class, DocumentsServiceTest.class, DocumentServiceTest.class,
    ChapterServiceTest.class, ProgressServiceTest.class, PersonsServiceTest.class,
    PersonServiceTest.class, PlacesServiceTest.class, PlaceServiceTest.class,
    DocumentPartsServiceTest.class, AnalysisServiceTest.class, EntityRelationsServiceTest.class,
    AttributesServiceTest.class, AttributeServiceTest.class, OccurrencesServiceTests.class,
    ParametersServiceTest.class})
public class ServiceTests {

}
