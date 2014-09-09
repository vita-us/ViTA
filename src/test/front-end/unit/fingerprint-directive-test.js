describe('Fingerprint Directive', function() {
  var scope, element;

  beforeEach(module('vita'));

  beforeEach(inject(function($rootScope, $compile, TestData) {

    scope = $rootScope.$new();

    element = '<fingerprint data="fingerprint"></fingerprint>';
    scope.fingerprint = TestData.fingerprint;

    element = $compile(element)(scope);
    scope.$digest();

  }));

  it('should create correct number of occurrence rects', inject(function(TestData) {

    expect(element.find('.occurrences').children().length).toBe(
            TestData.fingerprint.occurrences.length);

  }));

});
