describe('ChapterTextService', function() {

  beforeEach(function() {
    this.addMatchers({
      toEqualData: function(expected) {
        return angular.equals(this.actual, expected);
      }
    });
  });

  beforeEach(module('vita'));

  it('should return the text and info of a chapter', inject(function($httpBackend, ChapterText,
          TestData) {
    $httpBackend.expectGET('/documents/doc13a/chapters/chap123').respond(TestData.singleChapter);

    var result = ChapterText.get({
      documentId: 'doc13a',
      chapterId: 'chap123'
    });

    $httpBackend.flush();

    expect(result).toEqualData(TestData.singleChapter);
  }));

});
