describe('DocumentSearch', function() {

  beforeEach(function() {
    this.addMatchers({
      toEqualData: function(expected) {
        return angular.equals(this.actual, expected);
      }
    });
  });

  beforeEach(module('vita'));

  it('should expect a get request', inject(function($httpBackend, TestData, DocumentSearch) {
    $httpBackend.expectGET('/documents/doc13a/search?query=hugs+and+kisses').respond(
            TestData.search);

    var response = DocumentSearch.search({
      documentId: 'doc13a',
      query: 'hugs and kisses'
    });
    $httpBackend.flush();

    expect(response).toEqualData(TestData.search);
  }));

});
