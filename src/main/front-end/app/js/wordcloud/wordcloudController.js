(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  vitaControllers.controller('WordcloudCtrl', ['$scope', 'Wordcloud', 'Page', '$routeParams',
      'Document', 'Person', 'CssClass',
      function($scope, Wordcloud, Page, $routeParams, Document, Person, CssClass) {

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

        Document.get({
          documentId: $routeParams.documentId
        }, function(document) {
          Page.breadcrumbs = 'Wordcloud';
          Page.setUpForDocument(document);
        });

        Person.get({
          documentId: $routeParams.documentId
        }, function(response) {
          $scope.persons = response.persons;
        });

        $scope.loadPersonWordcloud = function(person) {
          $scope.activeWordcloud = person.id;
          $scope.wordcloudPerson = person;
          loadWordcloud(person.id);
        };

        $scope.loadGlobalWordcloud = function() {
          $scope.activeWordcloud = 'global';
          $scope.wordcloudPerson = undefined;
          loadWordcloud();
        };

        // initial setup
        $scope.loadGlobalWordcloud();

        function loadWordcloud(entityId) {
          Wordcloud.get({
            documentId: $routeParams.documentId,
            entityId: entityId
          }, function(response) {
            $scope.wordcloud = response.items;
          });
        }
      }]);
})(angular);
