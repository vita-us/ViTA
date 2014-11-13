describe('PersonCtrl', function() {
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
    $httpBackend.expectGET('webapi/documents/doc13a/persons/person8').respond(TestData.singlePerson);
    $httpBackend.expectGET('webapi/documents/doc13a').respond(TestData.singleDocument);

    $routeParams.documentId = 'doc13a';
    $routeParams.personId = 'person8';

    scope = $rootScope.$new();
    ctrl = $controller('PersonCtrl', {
      $scope: scope
    });
  }));

  it('should get a profile from REST by using the Person service', inject(function(TestData) {
    expect(scope.person).not.toBeDefined();
    $httpBackend.flush();
    expect(scope.person).toEqualData(TestData.singlePerson);
  }));

});
