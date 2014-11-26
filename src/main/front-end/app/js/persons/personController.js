(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the person page
  vitaControllers.controller('PersonCtrl',
    ['$scope', 'Document', 'Page', 'Person', '$routeParams', 'DocumentParts', 'Entity',
      function($scope, Document, Page, Person, $routeParams, DocumentParts, Entity) {

        $scope.relatedEntities = [];
        $scope.fingerprintIds = [];
        Person.get({
          documentId: $routeParams.documentId,
          personId: $routeParams.personId
        }, function(person) {
          $scope.person = person;
          $scope.fingerprintIds.push(person.id);

          for (var i = 0; i < $scope.person.entityRelations.length; i++) {
            retrieveEntity($scope.person.entityRelations[i].relatedEntity);
          }
          Page.breadcrumbs = 'Characters > ' + person.displayName;
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
