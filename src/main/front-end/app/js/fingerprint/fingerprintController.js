(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the fingerprint page
  vitaControllers.controller('FingerprintCtrl', ['$scope', 'Page', '$routeParams', 'DocumentParts',
      function($scope, Page, $routeParams, DocumentParts) {

        DocumentParts.get({
          documentId: $routeParams.documentId
        }, function(response) {
          $scope.parts = response.parts;
        });

        $scope.documentId = $routeParams.documentId;
        $scope.entityIds = ['34534', '3459'];
      }]);

})(angular);