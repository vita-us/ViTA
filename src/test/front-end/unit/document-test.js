describe('DocumentsCtrl', function() {
  var scope, $httpBackend, ctrl;

  beforeEach(function() {
    this.addMatchers({
      toEqualData: function(expected) {
        return angular.equals(this.actual, expected);
      }
    });
  });

  beforeEach(module('vita'));

  beforeEach(inject(function(_$httpBackend_, $rootScope, $controller, TestData) {
    $httpBackend = _$httpBackend_;
    $httpBackend.expectGET('/documents').respond(TestData.documents);
    scope = $rootScope.$new();
    ctrl = $controller('DocumentsCtrl', {
      $scope: scope
    });
  }));

  it('should create "document" model with 2 documents', inject(function(TestData) {

    expect(scope.documentsWrapper).toEqualData({});
    $httpBackend.flush();
    expect(scope.documentsWrapper.totalCount).toEqual(2);
    expect(scope.documents).toEqualData(TestData.documents.documents);

  }));

});
