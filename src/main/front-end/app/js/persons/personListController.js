(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the persons page
  vitaControllers.controller('PersonListCtrl', ['$scope', 'Document', 'Page', 'Person',
      '$routeParams', 'CssClass', function($scope, Document, Page, Person, $routeParams, CssClass) {
        Person.get({
          documentId: $routeParams.documentId
        }, function(response) {
          response.persons.forEach(function(entry) {
            entry.rankingValueClass = CssClass.forRankingValue(entry.rankingValue);
          });
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
