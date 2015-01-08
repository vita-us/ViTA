describe('Fingerprint Directive', function() {
  var scope, element;

  beforeEach(module('vita'));

  beforeEach(inject(function($rootScope, $compile, $httpBackend, TestData, $routeParams) {

    scope = $rootScope.$new();

    $httpBackend.expectGET(
            new RegExp('webapi/documents/123/entities/relations/occurrences\\?entityIds=456,789.*')).respond(
            TestData.fingerprint);

    scope.entityIds = ['456', '789'];
    scope.parts = TestData.parts.parts;
    $routeParams.documentId = '123';

    element = '<div data-fingerprint class="fingerprint-container" data-entity-ids="entityIds" ' +
              'data-parts="parts" data-range-begin="rangeStart" data-range-end="rangeEnd" ' +
              'style="width: 200px"></div>';

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
            TestData.parts.parts[0].chapters.length);
  }));

  it('should display the correct amount of part separators', inject(function(TestData) {
    expect(element.find('.part-separators').children().length).toBe(
        TestData.parts.parts.length);
  }));

  it('should update the number of occurrence rects when data changes', inject(function(TestData,
          $httpBackend) {

    var newFingerprintLength = 5;
    var newOccurrences = TestData.fingerprint.occurrences.slice(0, newFingerprintLength);
    var updatedFingerprintData = TestData.fingerprint;
    updatedFingerprintData.occurrences = newOccurrences;

    $httpBackend.expectGET(new RegExp('webapi/documents/123/entities/relations/occurrences\\?entityIds=999.*'))
            .respond(updatedFingerprintData);

    scope.entityIds = ['999'];
    element.scope().$apply();

    $httpBackend.flush();

    expect(element.find('.occurrences').children().length).toBe(newFingerprintLength);
  }));

  it('should update the number of chapter separators when parts change', inject(function(TestData) {
    var initialSeparatorCount = element.find('.chapter-separators').children().length;
    var newParts = TestData.parts.parts;
    newParts[0].chapters = newParts[0].chapters.slice(0, initialSeparatorCount - 1);
    element.scope().$apply();

    expect(element.find('.chapter-separators').children().length).toBe(initialSeparatorCount - 1);
  }));

  it('should display no occurence rect if the data is undefined', function() {
    scope.entityIds = undefined;
    element.scope().$apply();

    expect(element.find('.occurrences').children().length).toBe(0);
  });

  it('should display no chapter separator if the parts are undefined', function() {
    scope.parts = undefined;
    element.scope().$apply();

    expect(element.find('.chapter-separators').children().length).toBe(0);
  });

  it('should display no part separator if the parts are undefined', function() {
    scope.parts = undefined;
    element.scope().$apply();

    expect(element.find('.part-separators').children().length).toBe(0);
  });

  it('should update the range indicators', function() {
    // the + converts the string to a number
    expect(+element.find('#range-start-indicator').attr('width')).toBe(0);
    expect(+element.find('#range-end-indicator').attr('width')).toBe(0);

    scope.rangeStart = 0.3;
    scope.rangeEnd = 0.8;
    element.scope().$apply();

    expect(+element.find('#range-start-indicator').attr('width')).toBeGreaterThan(0);
    expect(+element.find('#range-end-indicator').attr('width')).toBeGreaterThan(0);
  });

});
