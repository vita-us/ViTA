describe('DocumentService', function() {

  beforeEach(function() {
    this.addMatchers({
      toEqualData: function(expected) {
        return angular.equals(this.actual, expected);
      }
    });
  });

  beforeEach(module('vita'));

  it('should return the document on get', inject(function($httpBackend, Document, TestData) {
    $httpBackend.expectGET('webapi/documents/doc13a').respond(TestData.singleDocument);

    var result = Document.get({
      documentId: 'doc13a'
    });
    $httpBackend.flush();

    expect(result).toEqualData(TestData.singleDocument);
  }));

  it('should expect a delete request', inject(function($httpBackend, Document) {
    $httpBackend.expectDELETE('webapi/documents/doc13a').respond(undefined);

    var result = Document.remove({
      documentId: 'doc13a'
    });
    $httpBackend.flush();
  }));

});
