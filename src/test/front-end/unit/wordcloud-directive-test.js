describe('Wordcloud Directive', function() {
  var scope, element;

  beforeEach(module('vita'));

  beforeEach(inject(function($rootScope, $compile, TestData) {

    scope = $rootScope.$new();

    element = '<div data-wordcloud data-items="wordcloud"></div>';
    scope.wordcloud = TestData.wordcloud.items;

    element = $compile(element)(scope);
    scope.$digest();

  }));

  it('should create correct number of words', inject(function(TestData) {

    expect(element.find('text').length).toBe(TestData.wordcloud.items.length);

  }));

  it('should update when the underlying data changes', function() {
    var newLength = 5;

    scope.wordcloud = scope.wordcloud.slice(0, newLength);
    element.scope().$apply();

    expect(element.find('text').length).toBe(newLength);

    scope.wordcloud.push({
      word: "Testword",
      frequency: 56,
    });

    element.scope().$apply();

    expect(element.find('text').length).toBe(newLength + 1);

  });

  it('should display nothing if the data are undefined', function() {
    scope.wordcloud = undefined;
    element.scope().$apply();

    expect(element.find('text').length).toBe(0);
  });

});
