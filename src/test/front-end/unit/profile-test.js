describe('ProfileCtrl', function() {
  var scope, $httpBackend, ctrl, singleProfileData = {
    "id": "person8Hugo",
    "displayName": "Hugo",
    "type": "person",
    "attributes": [{
      "id": "attr12397",
      "content": "15",
      "type": "age"
    }, {
      "id": "at65657",
      "content": "Hugo Martin",
      "type": "name"
    }],
    "rankingValue": 3,
    "entityRelations": [{
      "relatedEntity": "person10Ben",
      "weight": 0.81234
    }, {
      "relatedEntity": "person18Bert",
      "weight": 0.7345
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
    $httpBackend.expectGET('/documents/doc13a/persons/person8Hugo').respond(singleProfileData);

    $routeParams.documentId = 'doc13a';
    $routeParams.personId = 'person8Hugo';

    scope = $rootScope.$new();
    ctrl = $controller('ProfileCtrl', {
      $scope: scope
    });
  }));

  it('should get a profile from REST by using the Profiles service', inject(function() {
    expect(scope.profile).toEqualData({});
    $httpBackend.flush();
    expect(scope.profile).toEqualData(singleProfileData);
  }));
});
