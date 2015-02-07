(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the persons page
  vitaControllers.controller('PersonListCtrl',
    ['$scope', 'DocumentParts', 'Page', 'Person', 'Entity', '$routeParams',
      'CssClass', '$location', '$route', '$cacheFactory',
      function($scope, DocumentParts, Page, Person, Entity, $routeParams,
               CssClass, $location, $route, $cacheFactory) {

        var MAX_DISPLAYED_COOCCURRENCES = 5;

        $scope.persons = [];

        // Provide the service for direct usage in the scope
        $scope.CssClass = CssClass;

        // Create an empty selected character
        $scope.selected = {};

        // Entities related to the selected character
        $scope.relatedEntities = [];

        // Ids of the selected entities for the corresponding fingerprint
        $scope.fingerprintIds = [];

        // True if the related entities where downloaded from the server
        $scope.relatedEntitiesLoaded = false;

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
          $scope.relatedEntitiesLoaded = true;
          $scope.selected.compactAttributes = getCompactAttributes(person);
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

        var loadPersons = function() {
          Person.get({
            documentId: $routeParams.documentId
          }, function(response) {
            $scope.persons = response.persons;

            var selectedPerson = getPersonById($routeParams.personId);
            if (selectedPerson) {
              $scope.select(selectedPerson);
            } else if (response.totalCount > 0) {
              var firstPerson = response.persons[0];
              $location.path('documents/' + $routeParams.documentId + '/characters/' + firstPerson.id);
            }
          });
        };

        var getCompactAttributes = function(person) {
          var compactAttributes = [];
          var names = [];
          person.attributes.forEach(function(attribute) {
            if (attribute.attributetype === 'name') {
              if (attribute.content !== person.displayName) {
                names.push(attribute.content);
              }
            } else {
              compactAttributes.push(attribute);
            }
          });
          if (names.length > 0) {
            compactAttributes.push({attributetype: 'name', content: names.join(', ')});
          };
          return compactAttributes;
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

        loadPersons();

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

        $scope.deleteEntity = function() {
          var confirmed = confirm('Delete ' + $scope.selected.displayName + '?');
          if (!confirmed) {
            return;
          }
          Entity.remove({
            documentId: $routeParams.documentId,
            entityId: $scope.selected.id
          }, function() {
            var caches = ['person', 'place', 'entity', 'plotview', 'wordcloud'];
            caches.forEach(function(cacheId) {
              var cache = $cacheFactory.get(cacheId);
              if (cache) {
                cache.removeAll();
              }
            });
            loadPersons();
          });
        };

        Page.breadcrumbs = 'Characters';
        Page.setUpForCurrentDocument();

        DocumentParts.get({
          documentId: $routeParams.documentId
        }, function(response) {
          $scope.parts = response.parts;
        });
      }]);
})(angular);
