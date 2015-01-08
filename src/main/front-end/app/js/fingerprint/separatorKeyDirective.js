(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('separatorKey', function() {
    return {
      restrict: 'A',
      templateUrl: 'templates/separatorkey.html'
    };
  });

})(angular);
