(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('booleanParameter', [function() {
    return {
      restrict: 'A',
      scope: {
        parameter: '='
      },
      templateUrl: 'templates/booleanparameter.html'
    };
  }]);

})(angular);
