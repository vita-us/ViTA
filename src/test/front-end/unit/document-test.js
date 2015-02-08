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
    $httpBackend.expectGET('webapi/documents').respond(TestData.documents);
    $httpBackend.expectGET('webapi/analysis-parameters').respond();
    scope = $rootScope.$new();
    ctrl = $controller('DocumentsCtrl', {
      $scope: scope
    });
  }));

  it('should create "document" model with all documents', inject(function(TestData) {

    expect(scope.documents).not.toBeDefined();
    $httpBackend.flush();
    expect(scope.documents.length).toEqual(TestData.documents.totalCount);
    expect(scope.documents).toEqualData(TestData.documents.documents);

  }));

});
