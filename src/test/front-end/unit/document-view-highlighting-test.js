describe('documentHighlighter', function() {
  var scope, element;

  beforeEach(module('vita','templates'));

  beforeEach(inject(function($rootScope, $compile, TestData, $httpBackend) {

    scope = $rootScope.$new();
    $httpBackend.expectGET('webapi/documents/doc13a/chapters/1-1').respond(TestData.singleChapter);
    $httpBackend.expectGET('webapi/documents/doc13a/chapters/1-2').respond(TestData.secondChapter);
    $httpBackend.expectGET('webapi/documents/doc13a/chapters/1-3').respond(TestData.thirdChapter);

    element = '<div data-document-view-highlighter data-document-id="documentId" data-entities="entities"'
            + ' data-selected-occurrence-index="selectedOccurrenceIndex"'
            + ' data-occurrences="occurrences" data-parts="parts" class="col-sm-9 document-view-text">'
            + ' <div id="part-{{part.number}}" data-part data-ng-repeat="part in parts"'
            + ' data-part-data="part" data-document-id="documentId"></div></div>';

    scope.documentId = TestData.singleDocument.id;
    scope.parts = TestData.parts.parts;
    element = $compile(element)(scope);
    $httpBackend.flush();
    scope.$digest();

    scope.occurrences = TestData.relationOccurrences.occurrences;
    scope.selectedOccurrenceIndex = 0;
    scope.entities = TestData.entities;
    scope.$digest();
  }));

  it('should highlight correct number of occurrences', inject(function(TestData) {
    expect(element.find('span.occurrence').length).toBe(TestData.relationOccurrences.occurrences.length);
  }));

  it('should highlight correct number of entities', inject(function(TestData) {
    expect(element.find('span[class^="ranking"]').length).toBe(9);
  }));

  it('should highlight the correct text', inject(function(TestData) {
    expect(element.find('span.occurrence').first().text()).toBe('Mr. Bilbo Baggins');
    expect(element.find('span.occurrence').last().text()).toBe('Gandalf the Wizard');
  }));

  it('should highlight nothing if data is undefined', function() {
    scope.occurrences = undefined;
    scope.selectedOccurrenceIndex = undefined;
    scope.$digest();
    expect(element.find('span.occurrence').length).toBe(0);
  });

  it('should change highlight when data changes', inject(function(TestData) {
    scope.occurrences = TestData.relationOccurrences.occurrences.slice(0, 3);
    scope.$digest();
    expect(element.find('span.occurrence').length).toBe(3);
  }));

  it('should select first occurrence by default', function() {
    var selected = element.find('span.selected');
    expect(selected.length).toBe(1);
    expect(selected.attr('id')).toBe('occurrence-0');
  });

  it('should update selected occurrence when data changes', function(TestData) {
    scope.selectedOccurrenceIndex = 1;
    scope.$digest();

    var selected = element.find('span.selected');
    expect(selected.length).toBe(1);
    expect(selected.attr('id')).toBe('occurrence-1');
  });
});