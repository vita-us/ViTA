(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the places page
  vitaControllers.controller('PlaceListCtrl', ['$scope', 'Document', 'Page', 'Place',
      '$routeParams', function($scope, Document, Page, Place, $routeParams) {
        Place.get({
          documentId: $routeParams.documentId
        }, function(placesWrapper) {
          $scope.places = placesWrapper.places;
        });

        Document.get({
          documentId: $routeParams.documentId
        }, function(document) {
          $scope.document = document;
          Page.breadcrumbs = 'Places';
          Page.setUpForDocument(document);
        });
      }]);

})(angular);