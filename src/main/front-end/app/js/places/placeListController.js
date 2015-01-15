(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the places page
  vitaControllers.controller('PlaceListCtrl', ['$scope', 'Document', 'Page', 'Place',
      '$routeParams', function($scope, Document, Page, Place, $routeParams) {

        var MAX_DISPLAYED_COOCCURRENCES = 5;

	$scope.selected = {};

        $scope.relatedEntities = [];
        $scope.fingerprintIds = [];

	// Used to select a person from the list
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
          var displayedOccurrenceCount = Math.min(relations.length, MAX_DISPLAYED_COOCCURRENCES);
          for (var i = 0; i < displayedOccurrenceCount; i++) {
            retrieveEntity(relations[i].relatedEntity, i);
          }
	};

	var retrieveEntity = function(id, index) {
          var entity = Entity.get({
            documentId: $routeParams.documentId,
            entityId: id
          }, function(entity) {
	    if($scope.relatedEntities.indexOf(entity) == -1) {
              $scope.relatedEntities.push(entity);
	    }
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

	$scope.isSelected = function(place) {
	  return place == $scope.selected;
	};

	$scope.isMarked = function(id) {
          return ($scope.fingerprintIds.indexOf(id) > -1);
        };

        Place.get({
          documentId: $routeParams.documentId
        }, function(placesWrapper) {
          $scope.places = placesWrapper.places;
	  $scope.selected = placesWrapper.places[0];
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
