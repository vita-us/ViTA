describe('OverviewCtrl', function() {
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
    $httpBackend.expectGET('/documents/123').respond(TestData.singleDocument);
    $httpBackend.expectGET('/documents/123/progress').respond(TestData.documentProgress);

    $routeParams.documentId = '123';
    scope = $rootScope.$new();
    ctrl = $controller('OverviewCtrl', {
      $scope: scope
    });
  }));

  it('should create "document" model', inject(function($controller, TestData) {
    expect(scope.document).not.toBeDefined();
    $httpBackend.flush();
    expect(scope.document).toEqualData(TestData.singleDocument);
  }));

  it('should retrieve the analysis status', inject(function(TestData) {
    expect(scope.progress).not.toBeDefined();
    $httpBackend.flush();
    expect(scope.progress).toEqualData(TestData.documentProgress);
  }));

});
