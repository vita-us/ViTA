(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('loadingSpinner', function() {
    return {
      restrict: 'A',
      templateUrl: 'templates/loadingspinner.html'
    };
  });

})(angular);
