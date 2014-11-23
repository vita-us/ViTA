(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the place page
  vitaControllers.controller('PlaceCtrl',
      ['$scope', 'Document', 'Page', 'Place', '$routeParams',
        'DocumentParts', 'Person',
      function($scope, Document, Page, Place, $routeParams,
        DocumentParts, Person) {
        $scope.relatedEntities = [];
        Place.get({
          documentId: $routeParams.documentId,
          placeId: $routeParams.placeId
        }, function(place) {
          $scope.place = place;

        for (var i = 0; i < $scope.place.entityRelations.length; i++) {
          retrieveEntityName($scope.place.entityRelations[i].relatedEntity);
        }

          Page.breadcrumbs = 'Places > ' + place.displayName;
        });

        var retrieveEntityName = function(id) {
          var entity = Person.get({
            documentId: $routeParams.documentId,
            personId: id
          }, function(person) {
            $scope.relatedEntities.push(person);
          });
        };

        Document.get({
          documentId: $routeParams.documentId
        }, function(document) {
          $scope.document = document;
          Page.setUpForDocument(document);
        });

        DocumentParts.get({
          documentId: $routeParams.documentId
        }, function(response) {
          $scope.parts = response.parts;
        });
      }]);

})(angular);
