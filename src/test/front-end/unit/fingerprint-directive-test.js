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

});
