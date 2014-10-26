describe('Fingerprint Directive', function() {
  var scope, element;

  beforeEach(module('vita'));

  beforeEach(inject(function($rootScope, $compile, TestData) {

    scope = $rootScope.$new();

    element = '<div data-fingerprint class="fingerprint-container" data-occurrences="occurrences" data-parts="parts"></div>';
    scope.occurrences = TestData.fingerprint.occurrences;
    scope.parts = TestData.parts.parts;

    element = $compile(element)(scope);
    scope.$digest();

  }));

  it('should create correct number of occurrence rects', inject(function(TestData) {
    expect(element.find('.occurrences').children().length).toBe(
            TestData.fingerprint.occurrences.length);
  }));

  it('should create correct number of chapter separators', inject(function(TestData) {
    expect(element.find('.chapter-separators').children().length).toBe(
            TestData.parts.parts[0].chapters.length * 2);
  }));

  it('should update the number of occurrence rects when data changes', inject(function(TestData) {
    var inititalRectCount = element.find('.occurrences').children().length;

    scope.occurrences = TestData.fingerprint.occurrences.slice(0, inititalRectCount - 1);
    element.scope().$apply();

    expect(element.find('.occurrences').children().length).toBe(inititalRectCount - 1);
  }));

  it('should update the number of chapter separators when parts change', inject(function(TestData) {
    var initialSeparatorCount = element.find('.chapter-separators').children().length;
    var newParts = TestData.parts.parts;
    newParts[0].chapters = newParts[0].chapters.slice(0, initialSeparatorCount / 2 - 1);
    element.scope().$apply();

    // -2 because we create two line for every chapter
    expect(element.find('.chapter-separators').children().length).toBe(initialSeparatorCount - 2);
  }));

  it('should display no occurence rect if the data is undefined', inject(function(TestData) {
    scope.occurrences = undefined;
    element.scope().$apply();

    expect(element.find('.occurrences').children().length).toBe(0);
  }));

  it('should display no chapter separator if the parts are undefined', inject(function(TestData) {
    scope.parts = undefined;
    element.scope().$apply();

    expect(element.find('.chapter-separators').children().length).toBe(0);
  }));

});
