describe('Routing', function() {
  it('should route by default to the overview', function() {
    browser.get('index.html#/documents/testId');

    browser.getLocationAbsUrl().then(function(url) {
      expect(url.split('#')[1]).toBe('/documents/testId/overview');
    });
  });
});
