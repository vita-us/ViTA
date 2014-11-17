(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the person page
  vitaControllers.controller('PersonCtrl', ['$scope', 'Document', 'Page', 'Person', '$routeParams',
      function($scope, Document, Page, Person, $routeParams) {

        Person.get({
          documentId: $routeParams.documentId,
          personId: $routeParams.personId
        }, function(person) {
          $scope.person = person;
          Page.breadcrumbs = 'Characters > ' + person.displayName;
        });

        Document.get({
          documentId: $routeParams.documentId
        }, function(document) {
          $scope.document = document;
          Page.setUpForDocument(document);
        });
      }]);
})(angular);
