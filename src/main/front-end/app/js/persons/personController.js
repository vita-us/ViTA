(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the person page
  vitaControllers.controller('PersonCtrl', ['$scope', 'Document', 'Page', 'Person', '$routeParams',
      function($scope, Document, Page, Person, $routeParams) {

        $scope.relatedEntities = [];
        Person.get({
          documentId: $routeParams.documentId,
          personId: $routeParams.personId
        }, function(person) {
          $scope.person = person;

          for (var i = 0; i < $scope.person.entityRelations.length; i++) {
            retrieveEntityName($scope.person.entityRelations[i].relatedEntity);
            }
          Page.breadcrumbs = 'Characters > ' + person.displayName;
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
      }]);
})(angular);
