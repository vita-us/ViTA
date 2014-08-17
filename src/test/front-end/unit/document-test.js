describe('DocumentsCtrl', function() {
  var scope, $httpBackend, ctrl, documentsData = function() {
    return {
      totalCount: 2,
      documents: [{
        id: 'doc13a',
        metadata: {
          title: "Rotkaepchen",
          author: "Hans Mueller",
          publisher: "XY Verlag",
          publishYear: 1957,
          genre: "Fantasy",
          edition: "Limited Edition"
        }
      }, {
        id: 'doc14',
        metadata: {
          title: "Hans guck in die Luft",
          author: "Peter Mayer",
          publisher: "ABC Verlag",
          publishYear: 1968,
          genre: "Krimi",
          edition: "Standard"
        }
      }]
    }
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
    $httpBackend.expectGET('/documents').respond(documentsData());
    scope = $rootScope.$new();
    ctrl = $controller('DocumentsCtrl', {
      $scope: scope
    });
  }));

  it('should create "document" model with 2 documents', inject(function($controller) {

    expect(scope.documentsWrapper).toEqualData({});
    $httpBackend.flush();
    expect(scope.documentsWrapper.totalCount).toEqual(2);
    expect(scope.documents).toEqualData(documentsData().documents);

  }));

});
