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
    $routeParams.documentId = '123';
    scope = $rootScope.$new();
    ctrl = $controller('OverviewCtrl', {
      $scope: scope
    });
  }));

  it('should create "document" model', inject(function($controller, TestData) {

    expect(scope.document).toEqualData({});
    $httpBackend.flush();
    expect(scope.document).toEqualData(TestData.singleDocument);

  }));

});
