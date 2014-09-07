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
    $httpBackend.expectGET('/documents/123/456,789/fingerprints').respond(TestData.fingerprint);
    $routeParams.documentId = '123';
    scope = $rootScope.$new();
    ctrl = $controller('FingerprintCtrl', {
      $scope: scope
    });
  }));

  it('should create "fingerprint" model', inject(function($controller, TestData) {

    expect(scope.fingerprint).not.toBeDefined();
    $httpBackend.flush();
    expect(scope.fingerprint).toEqualData(TestData.fingerprint);

  }));

});
