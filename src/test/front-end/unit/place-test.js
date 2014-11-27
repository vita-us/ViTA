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
    $httpBackend.expectGET('webapi/documents/123/places/456').respond(TestData.singlePlace);
    $httpBackend.expectGET('webapi/documents/123').respond(TestData.singleDocument);
    $httpBackend.expectGET('webapi/documents/123/parts').respond(TestData.parts);
    $httpBackend.expectGET(new RegExp('webapi/documents/123/entities/')).respond(TestData.person);
    $httpBackend.expectGET(new RegExp('webapi/documents/123/entities/')).respond(TestData.person);
    $routeParams.documentId = '123';
    $routeParams.placeId = '456';
    scope = $rootScope.$new();
    ctrl = $controller('PlaceCtrl', {
      $scope: scope
    });
  }));

  it('should create "place" model', inject(function($controller, TestData) {

    expect(scope.place).not.toBeDefined();
    $httpBackend.flush();
    expect(scope.place).toEqualData(TestData.singlePlace);

  }));

});
