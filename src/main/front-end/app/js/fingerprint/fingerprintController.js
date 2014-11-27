(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the fingerprint page
  vitaControllers.controller('FingerprintCtrl',
      ['$scope', 'Page', '$routeParams', 'DocumentParts', 'Document',
      function($scope, Page, $routeParams, DocumentParts, Document) {
        Document.get({
          documentId: $routeParams.documentId
        }, function(document) {
          Page.breadcrumbs = 'Fingerprint';
          Page.setUpForDocument(document);
        });

        $scope.activeFingerprints = [];
        $scope.activeFingerprintIds = [];
        $scope.toggleFingerprint = function(person) {
          if ($scope.activeFingerprints.indexOf(person) > -1) {
            $scope.activeFingerprints.splice(
              $scope.activeFingerprints.indexOf(person), 1);
          } else {
            $scope.activeFingerprints.push(person);
          }
          $scope.activeFingerprintIds = $scope.activeFingerprints.map(function(e) {
            return e.id;
          });
        };

        DocumentParts.get({
          documentId: $routeParams.documentId
        }, function(response) {
          $scope.parts = response.parts;
        });

        $scope.deselectAll = function() {
          $scope.activeFingerprints = [];
          $scope.activeFingerprintIds = [];
        };
      }]);

})(angular);
