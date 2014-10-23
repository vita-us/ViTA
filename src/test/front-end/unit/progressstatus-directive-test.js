describe('Progress Status Directive', function() {
  var scope, element;

  beforeEach(module('templates', 'vitaDirectives'));

  beforeEach(inject(function($rootScope, $compile) {
    scope = $rootScope.$new();

    scope.progress = {
      isReady: false,
      progress: 0.52135
    };
    element = '<div data-progress-status data-status="progress" '
            + 'data-image="img/overview-preview-image-ph.png"'
            + 'data-title="Characters"></div>';

    element = $compile(element)(scope);
    scope.$digest();
  }));

  it('should show the progress until its ready', inject(function() {
    expect(element.find('.status').hasClass('ng-hide')).toBe(false);

    scope.progress = {
      isReady: true,
      progress: 1.0
    };
    element.scope().$apply();

    expect(element.find('.status').hasClass('ng-hide')).toBe(true);
  }));

  it('should show the percentage', inject(function($filter) {
    var elementAsHtml = element.html();
    var percentage = $filter('number')(scope.progress.progress * 100, 2);
    var percentageString = percentage + '%';

    expect(elementAsHtml).toContain(percentageString);
  }));

});
