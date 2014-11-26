(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the place page
  vitaControllers.controller('PlaceCtrl',
    ['$scope', 'Document', 'Page', 'Place', '$routeParams', 'DocumentParts', 'Person', 'Entity',
      function($scope, Document, Page, Place, $routeParams, DocumentParts, Person, Entity) {

        var MAX_DISPLAYED_COOCCURRENCES = 5;

        $scope.relatedEntities = [];
        $scope.fingerprintIds = [];

        Place.get({
          documentId: $routeParams.documentId,
          placeId: $routeParams.placeId
        }, function(place) {
          $scope.place = place;
          $scope.fingerprintIds.push(place.id);

          var relations = place.entityRelations;
          relations.sort(function(a, b) {
            // move relations with high weights to the front
            return b.weight - a.weight;
          });
          var displayedOccurrenceCount = Math.min(relations.length, MAX_DISPLAYED_COOCCURRENCES);
          for (var i = 0; i < displayedOccurrenceCount; i++) {
            retrieveEntity(relations[i].relatedEntity, i);
          }

          Page.breadcrumbs = 'Places > ' + place.displayName;
        });

        var retrieveEntity = function(id, index) {
          var entity = Entity.get({
            documentId: $routeParams.documentId,
            entityId: id
          }, function(entity) {
            $scope.relatedEntities[index] = entity;
          });
        };

        $scope.setFingerprint = function(id) {
          var position = $scope.fingerprintIds.indexOf(id);
          if (position > -1) {
            $scope.fingerprintIds.splice(position, 1);
          } else {
            $scope.fingerprintIds.push(id);
          }
        };

        $scope.isMarked = function(id) {
          return ($scope.fingerprintIds.indexOf(id) > -1);
        };

        $scope.path = function(type) {
          if (type === 'person') {
            return 'characters';
          }
          if (type === 'place') {
            return 'places';
          }
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
