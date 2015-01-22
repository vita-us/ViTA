(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('enumParameter', [function() {
    return {
      restrict: 'A',
      scope: {
        parameter: '='
      },
      templateUrl: 'templates/enumparameter.html'
    };
  }]);

})(angular);
