describe('WordcloudCtrl', function() {
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
    $httpBackend.expectGET('/documents/123/wordcloud').respond(TestData.wordcloud);
    $routeParams.documentId = '123';
    scope = $rootScope.$new();
    ctrl = $controller('WordcloudCtrl', {
      $scope: scope
    });
  }));

  it('should create "wordcloud" model', inject(function(TestData) {
    expect(scope.wordcloud).not.toBeDefined();
    $httpBackend.flush();
    expect(scope.wordcloud).toEqualData(TestData.wordcloud.items);
  }));

});
