(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the fingerprint page
  vitaControllers.controller('FingerprintCtrl', ['$scope', 'Fingerprint', 'Page', '$routeParams',
      'TestData', function($scope, Fingerprint, Page, $routeParams, TestData) {
        // TODO: Use a service instead
        $scope.parts = TestData.parts.parts;

        Fingerprint.get({
          documentId: $routeParams.documentId,
          entityIds: '456,789'
        }, function(fingerprint) {
          $scope.fingerprint = fingerprint;
        });
      }]);

})(angular);