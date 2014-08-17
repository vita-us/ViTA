describe('ProfilesCtrl', function() {
  var scope, $httpBackend, ctrl, profilesData = function() {
    return {
      "totalCount": 2,
      "persons": [
        {
          "id": "person8Hugo",
          "displayName": "Hugo",
          "type": "person",
          "rankingValue": 3
        },
        {
          "id": "person10Bert",
          "displayName": "Bert",
          "type": "person",
          "rankingValue": 7,
          "$promise":{},
          "$resolved": true
        }
      ]
    };
  };

  beforeEach(function() {
    this.addMatchers({
      toEqualData: function(expected) {
        return angular.equals(this.actual, expected);
      }
    });
  });

  beforeEach(module('vita'));

  beforeEach(inject(function(_$httpBackend_, $rootScope, $controller) {
    $httpBackend = _$httpBackend_;
    scope = $rootScope.$new();
    ctrl = $controller('ProfilesCtrl', {
      $scope: scope
    });
  }));

  it('ProfilesCtrl should get all profiles from REST by using the Profiles service', inject(function() {
    // angular will automatically send two http requests
    $httpBackend.expectGET('/documents/doc13a/persons?offset=1').respond(profilesData().persons);
    $httpBackend.expectGET('/documents/doc13a/persons/person10Bert').respond(profilesData().persons[1]);
    $httpBackend.flush();

    expect(JSON.stringify(scope.profiles)).toEqual(JSON.stringify(profilesData().persons));
  }));

  it('ProfilesCtrl should get a profile from REST by using the Profiles service', inject(function() {
    // angular will automatically send two http requests
    $httpBackend.expectGET('/documents/doc13a/persons?offset=1').respond(profilesData().persons);
    $httpBackend.expectGET('/documents/doc13a/persons/person10Bert').respond(profilesData().persons[1]);
    $httpBackend.flush();

    expect(JSON.stringify(scope.profile)).toEqual(JSON.stringify(profilesData().persons[1]));
  }));
});