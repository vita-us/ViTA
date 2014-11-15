(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the place page
  vitaControllers.controller('PlaceCtrl', ['$scope', 'Document', 'Page', 'Place', '$routeParams',
      function($scope, Document, Page, Place, $routeParams) {
        Place.get({
          documentId: $routeParams.documentId,
          placeId: $routeParams.placeId
        }, function(place) {
          $scope.place = place;
          Page.breadcrumbs = 'Places > ' + place.displayName;
        });

        Document.get({
          documentId: $routeParams.documentId
        }, function(document) {
          $scope.document = document;
          Page.setUpForDocument(document);
        });
      }]);

})(angular);