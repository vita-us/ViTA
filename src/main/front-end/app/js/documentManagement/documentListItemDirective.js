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
          scope.operationIconClass = 'glyphicon-repeat';
          break;
        case 'failed':
          scope.statusIconClass = 'glyphicon-exclamation-sign';
          scope.operationIconClass = 'glyphicon-repeat';
          break;
        case 'running':
          scope.statusIconClass = 'glyphicon-play-circle';
          scope.operationIconClass = 'glyphicon-ban-circle';
          break;
        case 'scheduled':
          scope.statusIconClass = 'glyphicon-time';
          scope.operationIconClass = 'glyphicon-ban-circle';
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
