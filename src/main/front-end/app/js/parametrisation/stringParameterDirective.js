(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('stringParameter', [function() {
    return {
      restrict: 'A',
      scope: {
        parameter: '='
      },
      templateUrl: 'templates/stringparameter.html'
    };
  }]);

})(angular);
