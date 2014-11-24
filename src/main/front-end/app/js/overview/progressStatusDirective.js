(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('progressStatus', function() {

    var directive = {
      restrict: 'EA',
      scope: {
        status: '=',
        image: '@',
        title: '@'
      },
      link: link,
      templateUrl: 'templates/progressstatus.html'
    };

    function link(scope) {
      scope.$watch('status', function() {
        setup(scope);
      });
    }

    function setup(scope) {
      var status = scope.status;

      scope.isVisible = status && !status.isReady;

      if (!scope.isVisible) {
        return;
      }

      if (status.isFailed) {
        scope.statusText = 'failed';
      } else {
        var progressPercentage = +status.progress * 100;

        // round on 2 decimal places
        scope.statusText = Math.round(progressPercentage * 100) / 100 + '%';
      }
    }

    return directive;
  });

})(angular);
