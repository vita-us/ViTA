(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the fingerprint page
  vitaControllers.controller('FingerprintCtrl',
      ['$scope', 'Page', '$routeParams', 'DocumentParts', 'Document', 'Person', 'CssClass',
      function($scope, Page, $routeParams, DocumentParts, Document, Person, CssClass) {

        // Provide the service for direct usage in the scope
        $scope.CssClass = CssClass;

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
            $scope.activeFingerprints.sort(
              function(a, b) {
                return a.rankingValue - b.rankingValue;
            });
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

        Person.get({
          documentId: $routeParams.documentId
        }, function(response) {
          $scope.persons = response.persons;
        });

        $scope.deselectAll = function() {
          $scope.activeFingerprints = [];
          $scope.activeFingerprintIds = [];
        };
      }]);

})(angular);
