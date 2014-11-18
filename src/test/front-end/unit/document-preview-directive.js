describe('Document Preview Directive', function() {
  var scope, element, document, chapter;

  beforeEach(module('templates', 'vita'));

  beforeEach(inject(function($rootScope, $compile, $httpBackend, TestData) {

    document = TestData.singleDocument;
    chapter = TestData.singleChapter;

    $httpBackend.expectGET('webapi/documents/' + document.id + '/parts').respond(TestData.parts);
    $httpBackend.expectGET('webapi/documents/' + document.id + '/chapters/' + chapter.id)
            .respond(chapter);

    scope = $rootScope.$new();

    scope.document = TestData.singleDocument;
    element = '<div data-document-preview data-document="document"></div>';

    element = $compile(element)(scope);
    scope.$digest();
    $httpBackend.flush();
  }));

  it('should show the text of a chapter', inject(function(TestData) {
    expect(element.text()).toContain(TestData.singleChapter.text);
  }));

});
