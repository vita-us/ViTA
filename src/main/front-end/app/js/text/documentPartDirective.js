(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('part', function() {
    function link(scope) {
      scope.title = scope.partData.title;
      scope.chapters = scope.partData.chapters;
    }

    return {
      restrict: 'A',
      scope: {
        partData: '=',
        documentId: '='
      },
      link: link,
      templateUrl: 'templates/part.html'

    };
  });

})(angular);
