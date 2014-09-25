(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the persons page
  vitaControllers.controller('PersonListCtrl', ['$scope', 'Document', 'Page', 'Person',
      '$routeParams', function($scope, Document, Page, Person, $routeParams) {
        Person.get({
          documentId: $routeParams.documentId
        }, function(response) {
          $scope.persons = response.persons;
        });

        Document.get({
          documentId: $routeParams.documentId
        }, function(document) {
          $scope.document = document;
          Page.breadcrumbs = 'Characters';
          Page.setUpForDocument(document);
        });
      }]);
})(angular);
