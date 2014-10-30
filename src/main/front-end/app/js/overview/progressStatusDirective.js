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
      templateUrl: 'templates/progressstatus.html'
    };

    return directive;
  });

})(angular);
