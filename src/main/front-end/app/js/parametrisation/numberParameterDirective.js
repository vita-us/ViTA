(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('numberParameter', [function() {
    return {
      restrict: 'A',
      scope: {
        parameter: '='
      },
      templateUrl: 'templates/numberparameter.html'
    };
  }]);

})(angular);
