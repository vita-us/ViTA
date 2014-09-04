describe('ProfileListCtrl', function() {
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
    $httpBackend.expectGET('/documents/doc13a/persons').respond(TestData.profiles);

    $routeParams.documentId = 'doc13a';

    scope = $rootScope.$new();
    ctrl = $controller('ProfileListCtrl', {
      $scope: scope
    });
  }));

  it('should get all profiles from REST by using the Profiles service', inject(function(TestData) {
    expect(scope.profiles).not.toBeDefined();
    $httpBackend.flush();
    expect(scope.profiles).toEqualData(TestData.profiles.persons);
  }));

});
