(function(angular, $) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('menuScrollHighlighting', function() {

    var directive = {
      restrict: 'A',
      scope: {
        scrollElementId: '@'
      },
      link: link
    };

    function link(scope, element, attrs) {
      // credits for concept: http://jsfiddle.net/cse_tushar/Dxtyu/141/

      var scrollContainer = $('#' + scope.scrollElementId);

      scrollContainer.on('scroll', function(event) {
        var visibleTop = scrollContainer.scrollTop();

        var anchorTags = element.find('a');
        anchorTags.parent().removeClass('active-menu');

        anchorTags.each(function() {
          var anchorTag = $(this);

          var referedElement = $(anchorTag.attr('href'));

          var elementTop = referedElement.position().top;
          var elementBottom = elementTop + referedElement.height();

          // Check whether the element is currently visible
          if (elementTop <= visibleTop && elementBottom > visibleTop) {
            anchorTag.parent().addClass('active-menu');
          } else {
            anchorTag.parent().removeClass('active-menu');
          }
        });
      });
    }

    return directive;
  });

})(angular, jQuery);
