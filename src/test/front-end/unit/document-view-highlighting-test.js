describe('documentHighlighter', function() {
  var scope, element, $httpBackend;

  beforeEach(module('vita','templates'));

  beforeEach(inject(function($rootScope, $compile, TestData, _$httpBackend_) {

    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    $httpBackend.expectGET('webapi/documents/doc13a/chapters/1.1').respond(TestData.singleChapter);
    $httpBackend.expectGET('webapi/documents/doc13a/chapters/1.2').respond(TestData.secondChapter);
    $httpBackend.expectGET('webapi/documents/doc13a/chapters/1.3').respond(TestData.thirdChapter);

    element = '<div data-document-view-highlighter data-document-id="documentId"'
            + ' data-occurrences="occurrences" class="col-sm-9 document-view-text"><div id="part-{{part.number}}"' 
            + ' data-part data-ng-repeat="part in parts" data-part-data="part"'
            + ' data-document-id="documentId"></div></div>';
    scope.documentId = TestData.singleDocument.id;
    scope.parts = TestData.parts.parts;
    element = $compile(element)(scope);
    scope.$digest();
    $httpBackend.flush();

    // atm no clue why it is just working with multiple expected requests
    $httpBackend.expectGET('webapi/documents/doc13a/chapters/1.1').respond(TestData.singleChapter);
    $httpBackend.expectGET('webapi/documents/doc13a/chapters/1.2').respond(TestData.secondChapter);
    $httpBackend.expectGET('webapi/documents/doc13a/chapters/1.3').respond(TestData.thirdChapter);
    $httpBackend.expectGET('webapi/documents/doc13a/chapters/1.1').respond(TestData.singleChapter);
    $httpBackend.expectGET('webapi/documents/doc13a/chapters/1.2').respond(TestData.secondChapter);
    $httpBackend.expectGET('webapi/documents/doc13a/chapters/1.3').respond(TestData.thirdChapter);
    scope.occurrences = TestData.relationOccurrences.occurrences;
    scope.$digest();
    $httpBackend.flush();
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
    scope.$digest();
    expect(element.find('span.occurrence').length).toBe(0);
  });

  it('should change highlight when data changes', inject(function(TestData) {
    $httpBackend.expectGET('webapi/documents/doc13a/chapters/1.1').respond(TestData.singleChapter);
    scope.occurrences = TestData.relationOccurrences.occurrences.slice(0, 3);
    scope.$digest();
    $httpBackend.flush();
    expect(element.find('span.occurrence').length).toBe(3);
  }));
});