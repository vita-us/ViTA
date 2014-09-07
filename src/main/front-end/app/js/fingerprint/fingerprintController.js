(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the fingerprint page
  vitaControllers.controller('FingerprintCtrl', ['$scope', 'Fingerprint', 'Page', '$routeParams',
      function($scope, Fingerprint, Page, $routeParams) {
        Fingerprint.get({
          documentId: $routeParams.documentId,
          entityIds: '456,789'
        }, function(fingerprint) {
          $scope.fingerprint = fingerprint;
        });
      }]);

})(angular);