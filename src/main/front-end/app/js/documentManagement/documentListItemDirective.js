(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('documentListItem', function() {

    var directive = {
      restrict: 'A',
      scope: {
        document: '='
      },
      link: function(scope, element, attrs) {

        switch (scope.document.progress.status) {
        case 'cancelled':
          scope.statusIconClass = 'glyphicon-remove-circle';
          break;
        case 'failed':
          scope.statusIconClass = 'glyphicon-exclamation-sign';
          break;
        case 'running':
          scope.statusIconClass = 'glyphicon-play-circle';
          break;
        case 'scheduled':
          scope.statusIconClass = 'glyphicon-time';
          break;
        case 'success':
          scope.statusIconClass = 'glyphicon-ok-circle';
          break;
        }

      },
      templateUrl: 'templates/documentlistitem.html'
    };
    return directive;
  });

})(angular);
