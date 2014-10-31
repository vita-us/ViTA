describe('Fingerprint Directive', function() {
  var scope, element;

  beforeEach(module('vita'));

  beforeEach(inject(function($rootScope, $compile, _$httpBackend_, TestData) {

    scope = $rootScope.$new();

    $httpBackend = _$httpBackend_;
    $httpBackend.expectGET('/documents/123/456,789/fingerprints?steps=0').respond(
            TestData.fingerprint);

    scope.entityIds = '456,789';
    scope.documentId = '123';
    scope.parts = TestData.parts.parts;

    element = '<div data-fingerprint class="fingerprint-container" data-document-id="documentId"'
            + 'data-entity-ids="entityIds" data-parts="parts"></div>';

    element = $compile(element)(scope);
    scope.$digest();
    $httpBackend.flush();
  }));

  it('should create correct number of occurrence rects', inject(function(TestData) {
    expect(element.find('.occurrences').children().length).toBe(
            TestData.fingerprint.occurrences.length);
  }));

  it('should create correct number of chapter separators', inject(function(TestData) {
    expect(element.find('.chapter-separators').children().length).toBe(
            TestData.parts.parts[0].chapters.length * 2);
  }));

  it('should update the number of occurrence rects when data changes', inject(function(TestData,
          _$httpBackend_) {
    $httpBackend = _$httpBackend_;

    var newFingerprintLength = 5;
    var newOccurrences = TestData.fingerprint.occurrences.slice(0, newFingerprintLength);
    var updatedFingerprintData = TestData.fingerprint;
    updatedFingerprintData.occurrences = newOccurrences;

    $httpBackend.expectGET('/documents/123/999/fingerprints?steps=0').respond(
            updatedFingerprintData);

    scope.entityIds = '999';
    element.scope().$apply();

    $httpBackend.flush();

    expect(element.find('.occurrences').children().length).toBe(newFingerprintLength);
  }));

  it('should update the number of chapter separators when parts change', inject(function(TestData) {
    var initialSeparatorCount = element.find('.chapter-separators').children().length;
    var newParts = TestData.parts.parts;
    newParts[0].chapters = newParts[0].chapters.slice(0, initialSeparatorCount / 2 - 1);
    element.scope().$apply();

    // -2 because we create two line for every chapter
    expect(element.find('.chapter-separators').children().length).toBe(initialSeparatorCount - 2);
  }));

  it('should display no occurence rect if the data is undefined', inject(function(_$httpBackend_) {
    $httpBackend = _$httpBackend_;
    $httpBackend.expectGET(/.*/).respond(undefined);

    scope.entityIds = undefined;
    element.scope().$apply();

    $httpBackend.flush();
    expect(element.find('.occurrences').children().length).toBe(0);
  }));

  it('should display no chapter separator if the parts are undefined', function() {
    scope.parts = undefined;
    element.scope().$apply();

    expect(element.find('.chapter-separators').children().length).toBe(0);
  });

});
