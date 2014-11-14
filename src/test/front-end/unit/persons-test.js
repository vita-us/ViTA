describe('PersonListCtrl', function() {
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
    $httpBackend.expectGET('/documents/doc13a/persons').respond(TestData.persons);
    $httpBackend.expectGET('/documents/doc13a').respond(TestData.singleDocument);

    $routeParams.documentId = 'doc13a';

    scope = $rootScope.$new();
    ctrl = $controller('PersonListCtrl', {
      $scope: scope
    });
  }));

  it('should get all profiles from REST by using the Person service', inject(function(TestData) {
    expect(scope.persons).not.toBeDefined();
    $httpBackend.flush();
    expect(scope.persons).toEqualData(TestData.persons.persons);
  }));

});
