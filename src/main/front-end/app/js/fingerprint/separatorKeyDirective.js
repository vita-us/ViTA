(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('separatorKey', ['FingerprintSynchronizer', function(FingerprintSynchronizer) {
    function link(scope) {
      scope.FingerprintSynchronizer = FingerprintSynchronizer;
    }

    return {
      restrict: 'A',
      templateUrl: 'templates/separatorkey.html',
      link: link
    };
  }]);

})(angular);
