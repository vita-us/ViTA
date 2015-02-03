(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the persons page
  vitaControllers.controller('PersonListCtrl',
    ['$scope', 'DocumentParts', 'Page', 'Person', 'Entity', '$routeParams', 'CssClass',
      function($scope, DocumentParts, Page, Person, Entity, $routeParams, CssClass) {

        var MAX_DISPLAYED_COOCCURRENCES = 5;

        // Provide the service for direct usage in the scope
        $scope.CssClass = CssClass;

        // Create an empty selected character
        $scope.selected = {};

        // Entities related to the selected character
        $scope.relatedEntities = [];

        // Ids of the selected entities for the corresponding fingerprint
        $scope.fingerprintIds = [];

        // Used to select a person from the list
        $scope.select = function(person) {
          $scope.selected = person;
          $scope.fingerprintIds = [];
          $scope.relatedEntities = [];
          $scope.fingerprintIds.push(person.id);

          var relations = person.entityRelations;
          relations.sort(function(a, b) {
            // move relations with high weights to the front
            return b.weight - a.weight;
          });
          var displayedOccurrenceCount = Math.min(relations.length, MAX_DISPLAYED_COOCCURRENCES);
          for (var i = 0; i < displayedOccurrenceCount; i++) {
            retrieveEntity(relations[i].relatedEntity, i);
          }
        };

        var retrieveEntity = function(id) {
          Entity.get({
            documentId: $routeParams.documentId,
            entityId: id
          }, function(entity) {
            if ($scope.relatedEntities.indexOf(entity) == -1) {
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

        $scope.isSelected = function(person) {
          return person == $scope.selected;
        };

        $scope.isMarked = function(id) {
          return ($scope.fingerprintIds.indexOf(id) > -1);
        };

        // Get a list of characters from the server
        Person.get({
          documentId: $routeParams.documentId
        }, function(response) {
          $scope.persons = response.persons;
          var selectedPerson = getPersonById($routeParams.personId);
          if (selectedPerson) {
            $scope.select(selectedPerson);
          } else {
          $scope.select(response.persons[0]);
          }
        });

        var getPersonById = function(id) {
          for (var i = 0; i < $scope.persons.length; i++) {
            if ($scope.persons[i].id == id) {
              return $scope.persons[i];
            }
          }
          return undefined;
        };

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

        DocumentParts.get({
          documentId: $routeParams.documentId
        }, function(response) {
          $scope.parts = response.parts;
        });
      }]);
})(angular);
