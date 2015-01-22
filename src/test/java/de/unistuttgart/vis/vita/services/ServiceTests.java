package de.unistuttgart.vis.vita.services;

import de.unistuttgart.vis.vita.services.analysis.ParametersServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.unistuttgart.vis.vita.services.analysis.AnalysisServiceTest;
import de.unistuttgart.vis.vita.services.analysis.ProgressServiceTest;
import de.unistuttgart.vis.vita.services.document.ChapterServiceTest;
import de.unistuttgart.vis.vita.services.document.DocumentPartsServiceTests;
import de.unistuttgart.vis.vita.services.document.DocumentServiceTest;
import de.unistuttgart.vis.vita.services.document.DocumentsServiceTest;
import de.unistuttgart.vis.vita.services.entity.AttributeServiceTest;
import de.unistuttgart.vis.vita.services.entity.AttributesServiceTest;
import de.unistuttgart.vis.vita.services.entity.EntityRelationsServiceTest;
import de.unistuttgart.vis.vita.services.entity.PersonServiceTest;
import de.unistuttgart.vis.vita.services.entity.PersonsServiceTests;
import de.unistuttgart.vis.vita.services.entity.PlaceServiceTest;
import de.unistuttgart.vis.vita.services.entity.PlacesServiceTests;
import de.unistuttgart.vis.vita.services.entity.PlotViewServiceTests;
import de.unistuttgart.vis.vita.services.occurrence.OccurrencesServiceTests;
import de.unistuttgart.vis.vita.services.search.SearchInDocumentServiceTests;

/**
 * A suite containing the tests for the REST API
 */
@RunWith(Suite.class)
@SuiteClasses({VersionServiceTest.class, DocumentsServiceTest.class, DocumentServiceTest.class,
    ChapterServiceTest.class, ProgressServiceTest.class, PersonsServiceTests.class,
    PersonServiceTest.class, PlacesServiceTests.class, PlaceServiceTest.class,
    DocumentPartsServiceTests.class, AnalysisServiceTest.class, EntityRelationsServiceTest.class,
    AttributesServiceTest.class, AttributeServiceTest.class, OccurrencesServiceTests.class,
    ParametersServiceTest.class, WordCloudServiceTests.class, PlotViewServiceTests.class,
    SearchInDocumentServiceTests.class})
public class ServiceTests {

}
