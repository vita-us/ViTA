(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the fingerprint page
  vitaControllers.controller('FingerprintCtrl', ['$scope', 'Page', '$routeParams', 'DocumentParts',
      'Document', function($scope, Page, $routeParams, DocumentParts, Document) {
        Document.get({
          documentId: $routeParams.documentId
        }, function(document) {
          Page.breadcrumbs = 'Fingerprint';
          Page.setUpForDocument(document);
        });

        DocumentParts.get({
          documentId: $routeParams.documentId
        }, function(response) {
          $scope.parts = response.parts;
        });

        $scope.entityIds = ['34534', '3459'];
      }]);

})(angular);
