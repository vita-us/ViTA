describe('PlaceCtrl', function() {
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
    $httpBackend.expectGET('/documents/123/places/456').respond(TestData.singlePlace);
    $routeParams.documentId = '123';
    $routeParams.placeId = '456';
    scope = $rootScope.$new();
    ctrl = $controller('PlaceCtrl', {
      $scope: scope
    });
  }));

  it('should create "place" model', inject(function($controller, TestData) {

    expect(scope.place).toEqualData({});
    $httpBackend.flush();
    expect(scope.place).toEqualData(TestData.singlePlace);

  }));

});
