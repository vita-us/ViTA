(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  vitaControllers.controller('WordcloudCtrl', ['$scope', 'Wordcloud', 'Page', '$routeParams',
      'Document', 'Person', function($scope, Wordcloud, Page, $routeParams, Document, Person) {
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
          loadWordcloud(person.id);
        };

        $scope.loadGlobalWordcloud = function() {
          $scope.activeWordcloud = 'global';
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
