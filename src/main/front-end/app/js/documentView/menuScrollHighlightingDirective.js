(function(angular, $) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('menuScrollHighlighting', function() {

    var directive = {
      restrict: 'A',
      scope: {
        menuSelector: '@',
        scrollContainerId: '@'
      },
      link: link
    };

    function link(scope, menuContainer) {
      // credits for concept: http://jsfiddle.net/cse_tushar/Dxtyu/141/

      var scrollContainer = $('#' + scope.scrollContainerId);

      scrollContainer.on('scroll', function(event) {
        // a position in the scrollable area, which is currently visible for
        // the user - in this case it's the top position
        var visibleTopPosition = scrollContainer.scrollTop();

        var menuItems = menuContainer.find(scope.menuSelector);
        menuItems.removeClass('active-menu');

        menuItems.each(function() {
          var menu = $(this);

          var referedElement = $(menu.find('a').attr('href'));

          var elementTop = referedElement.position().top;
          var elementBottom = elementTop + referedElement.height();

          // Check whether the element is currently visible
          if (elementTop <= visibleTopPosition && elementBottom > visibleTopPosition) {
            menu.addClass('active-menu');
          }
        });
      });
    }

    return directive;
  });

})(angular, jQuery);
