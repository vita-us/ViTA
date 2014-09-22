(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the place page
  vitaControllers.controller('PlaceCtrl', ['$scope', 'Document', 'Page', 'Place', '$routeParams',
      function($scope, Document, Page, Place, $routeParams) {
        var placeName = '';
        
        Place.get({
          documentId: $routeParams.documentId,
          placeId: $routeParams.placeId
        }, function(singlePlace) {
          $scope.place = singlePlace;
          placeName = singlePlace.displayName;
        });
        
        Document.get({
          documentId: $routeParams.documentId
        }, function(document) {
          $scope.document = document;
          Page.breadcrumbs = 'Places > ' + placeName;
          Page.setUpForDocument(document);
        });
      }]);

})(angular);