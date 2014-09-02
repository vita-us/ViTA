describe('ProfilesCtrl', function() {
  var scope, $httpBackend, ctrl, profilesData = {
    "totalCount": 2,
    "persons": [{
      "id": "person8Hugo",
      "displayName": "Hugo",
      "type": "person",
      "rankingValue": 3
    }, {
      "id": "person10Bert",
      "displayName": "Bert",
      "type": "person",
      "rankingValue": 7
    }]
  };

  beforeEach(function() {
    this.addMatchers({
      toEqualData: function(expected) {
        return angular.equals(this.actual, expected);
      }
    });
  });

  beforeEach(module('vita'));

  beforeEach(inject(function(_$httpBackend_, $rootScope, $controller, $routeParams) {
    $httpBackend = _$httpBackend_;
    $httpBackend.expectGET('/documents/doc13a/persons').respond(profilesData);

    $routeParams.documentId = 'doc13a';

    scope = $rootScope.$new();
    ctrl = $controller('ProfilesCtrl', {
      $scope: scope
    });
  }));

  it('should get all profiles from REST by using the Profiles service', inject(function() {
    expect(scope.profiles).not.toBeDefined();
    $httpBackend.flush();
    expect(scope.profiles).toEqualData(profilesData.persons);
  }));

});
