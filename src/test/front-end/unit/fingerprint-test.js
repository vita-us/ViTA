describe('FingerprintCtrl', function() {
  var scope, $httpBackend, ctrl;

  beforeEach(function() {
    this.addMatchers({
      toEqualData: function(expected) {
        return angular.equals(this.actual, expected);
      }
    });
  });

  beforeEach(module('vita'));

  beforeEach(inject(function(_$httpBackend_, $rootScope, $controller, $routeParams, TestData) {
    $httpBackend = _$httpBackend_;
    $httpBackend.expectGET('webapi/documents/123/parts').respond(TestData.parts);
    $httpBackend.expectGET('webapi/documents/123/456,789/fingerprints').respond(TestData.fingerprint);
    $routeParams.documentId = '123';
    scope = $rootScope.$new();
    ctrl = $controller('FingerprintCtrl', {
      $scope: scope
    });
  }));

  it('should create "fingerprint" model', inject(function($controller, TestData) {
    expect(scope.occurrences).not.toBeDefined();
    $httpBackend.flush();
    expect(scope.occurrences).toEqualData(TestData.fingerprint.occurrences);
  }));

  it('should receive the document parts', inject(function($controller, TestData) {
    expect(scope.parts).not.toBeDefined();
    $httpBackend.flush();
    expect(scope.parts).toEqualData(TestData.parts.parts);
  }));

});
