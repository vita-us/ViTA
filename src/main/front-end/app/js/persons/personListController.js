(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the persons page
  vitaControllers.controller('PersonListCtrl', ['$scope', 'Document', 'Page', 'Person',
      '$routeParams', 'CssClass', function($scope, Document, Page, Person, $routeParams, CssClass) {

        // Provide the service for direct usage in the scope
        $scope.CssClass = CssClass;

        $scope.alternativeNames = function(person, searchQuery) {
          for (var i = 0; i < person.attributes.length; i++) {
            var attribute = person.attributes[i];
            if (attribute.attributetype == 'name' &&
              containsQueryCaseInsensitive(attribute.content, searchQuery)) {
              person.alternativeName = attribute.content;
            }
          }
        };

        var containsQueryCaseInsensitive = function(text, query) {
          var lowerCaseText = text.toLowerCase();
          var lowerCaseQuery = query.toLowerCase();

          return lowerCaseText.indexOf(lowerCaseQuery) > -1;
        };

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
