(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the place page
  vitaControllers.controller('PlaceCtrl',
    ['$scope', 'Document', 'Page', 'Place', '$routeParams', 'DocumentParts', 'Person', 'Entity',
      function($scope, Document, Page, Place, $routeParams, DocumentParts, Person, Entity) {

        $scope.relatedEntities = [];
        $scope.fingerprintIds = [];
        Place.get({
          documentId: $routeParams.documentId,
          placeId: $routeParams.placeId
        }, function(place) {
          $scope.place = place;
          $scope.fingerprintIds.push(place.id);

        for (var i = 0; i < $scope.place.entityRelations.length; i++) {
          retrieveEntity($scope.place.entityRelations[i].relatedEntity);
        }

          Page.breadcrumbs = 'Places > ' + place.displayName;
        });

        var retrieveEntity = function(id) {
          var entity = Entity.get({
            documentId: $routeParams.documentId,
            entityId: id
          }, function(entity) {
            $scope.relatedEntities.push(entity);
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
