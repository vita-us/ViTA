describe('OverviewCtrl', function() {
  var scope, $httpBackend, ctrl, documentData = function() {
    return {
      id: "doc13a",
      metadata: {
        title: "Rotkaeppchen",
        author: "Hans Mueller",
        publisher: "XY Verlag",
        publishYear: 1957,
        genre: "Thriller",
        edition: "Limited Edition"
      },
      metrics: {
        characterCount: 19231,
        chapterCount: 3,
        personCount: 8,
        placeCount: 2,
        wordCount: 1999
      },
      content: {
        parts: {
          chapters: ["chapter1", "chapter2", "chapter3"],
          number: 1,
          title: "Der erste Teil"
        },
        persons: ["person8Ben", "person10Bert"],
        places: ["place2Paris", "place10Mordor"]
      }
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
  beforeEach(module('vitaServices'));

  beforeEach(inject(function(_$httpBackend_, $rootScope, $controller) {
    $httpBackend = _$httpBackend_;
    $httpBackend.expectGET('test_data/single-document.json').respond(documentData());
    scope = $rootScope.$new();
    ctrl = $controller('OverviewCtrl', {
      $scope: scope
    });
  }));

  it('should create "document" model', inject(function($controller) {

    expect(scope.document).toEqualData({});
    $httpBackend.flush();
    expect(scope.document).toEqualData(documentData());

  }));

});
