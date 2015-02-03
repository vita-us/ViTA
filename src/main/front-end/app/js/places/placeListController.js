(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the persons page
  vitaControllers.controller('PlaceListCtrl',
    ['$scope', 'DocumentParts', 'Page', 'Place', 'Entity', '$routeParams', 'CssClass',
      function($scope, DocumentParts, Page, Place, Entity, $routeParams, CssClass) {

        var MAX_DISPLAYED_COOCCURRENCES = 5;

        // Provide the service for direct usage in the scope
        $scope.CssClass = CssClass;

        // Create an empty selected character
        $scope.selected = {};

        // Entities related to the selected character
        $scope.relatedEntities = [];

        // Ids of the selected entities for the corresponding fingerprint
        $scope.fingerprintIds = [];

        // Used to select a place from the list
        $scope.select = function(place) {
          $scope.selected = place;
          $scope.fingerprintIds = [];
          $scope.relatedEntities = [];
          $scope.fingerprintIds.push(place.id);

          var relations = place.entityRelations;
          relations.sort(function(a, b) {
            // move relations with high weights to the front
            return b.weight - a.weight;
          });

          // get the related entities from the server
          var displayedOccurrenceCount = Math.min(relations.length, MAX_DISPLAYED_COOCCURRENCES);
          for (var i = 0; i < displayedOccurrenceCount; i++) {
            retrieveEntity(relations[i].relatedEntity);
          }
        };

        // Load an Entity from the server and add it to the related entities if not already there
        var retrieveEntity = function(id) {
          var entity = Entity.get({
            documentId: $routeParams.documentId,
            entityId: id
          }, function(entity) {
            if ($scope.relatedEntities.indexOf(entity) == -1) {
              $scope.relatedEntities.push(entity);
          }
          });
        };

        // Adds the entity to the fingerprint
        $scope.setFingerprint = function(id) {
          var position = $scope.fingerprintIds.indexOf(id);
          if (position > -1) {
            $scope.fingerprintIds.splice(position, 1);
          } else {
            $scope.fingerprintIds.push(id);
          }
        };

        // Returns true if the given place is the same as the one currently displayed
        $scope.isSelected = function(place) {
          return place == $scope.selected;
        };

        // Returns true if the Entity is visualized in the fingerprint
        $scope.isMarked = function(id) {
          return ($scope.fingerprintIds.indexOf(id) > -1);
        };

        // Get a list of characters from the server
        Place.get({
          documentId: $routeParams.documentId
        }, function(response) {
          $scope.places = response.places;

          // select the place specified in the id
          // if not specified use the place with the highest ranking value
          var selectedPlace = getPlaceById($routeParams.placeId);

          if (selectedPlace) {
            $scope.select(selectedPlace);
          } else {
            $scope.select(response.places[0]);
          }
        });

        // Retrieve a place from all places by the given id
        var getPlaceById = function(id) {
          for (var i = 0; i < $scope.places.length; i++) {
            if ($scope.places[i].id == id) {
              return $scope.places[i];
            }
          }
          return undefined;
        };

        // Get the Entity-Type string of an entity used in the url
        $scope.getEntityType = function(entity) {
          if (entity.type === 'person') {
            return 'characters';
          }
          if (entity.type === 'place') {
            return 'places';
          }
        };

        Page.breadcrumbs = 'Characters';
        Page.setUpForDocument($routeParams.documentId);

        // Get the parts of the currect document from the server
        DocumentParts.get({
          documentId: $routeParams.documentId
        }, function(response) {
          $scope.parts = response.parts;
        });
      }]);
})(angular);
