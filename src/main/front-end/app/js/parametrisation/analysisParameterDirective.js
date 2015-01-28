(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('analysisParameter', function() {
    return {
      restrict: 'A',
      scope: {
        analysisParameters: '='
      },
      templateUrl: 'templates/analysisparameter.html'
    };
  });

})(angular);
