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
      var status = scope.status;

      scope.isVisible = status && !status.isReady;

      if (status.isFailed) {
        scope.statusText = 'failed';
      } else {
        var progressPercentage = +status.progress * 100;

        // round on 2 decimal places
        scope.statusText = Math.round(progressPercentage * 100) / 100 + '%';
      }
      console.log(scope.status);

    }

    return directive;
  });

})(angular);
