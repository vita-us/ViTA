(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the fingerprint page
  vitaControllers.controller('FingerprintCtrl', ['$scope', 'Fingerprint', 'Page', '$routeParams',
      'DocumentParts', function($scope, Fingerprint, Page, $routeParams, DocumentParts) {

        $scope.activeFingerprints = [];
        $scope.onclick = function(id) {
      if( $scope.activeFingerprints.indexOf(id) > -1 ) {
        $scope.activeFingerprints.splice($scope.activeFingerprints.indexOf(id),1);
        } else {
          $scope.activeFingerprints.push(id);
          }
          alert($scope.activeFingerprints);
        };

        DocumentParts.get({
          documentId: $routeParams.documentId
        }, function(response) {
          $scope.parts = response.parts;
        });

        Fingerprint.get({
          documentId: $routeParams.documentId,
          entityIds: '456,789'
        }, function(response) {
          $scope.occurrences = response.occurrences;
        });
      }]);

})(angular);
