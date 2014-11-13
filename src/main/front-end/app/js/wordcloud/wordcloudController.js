(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the wordcloud page
  vitaControllers.controller('WordcloudCtrl', ['$scope', 'Wordcloud', 'Page', '$routeParams',
      'Document', function($scope, Wordcloud, Page, $routeParams, Document) {
        Document.get({
          documentId: $routeParams.documentId
        }, function(document) {
          Page.breadcrumbs = 'Wordcloud';
          Page.setUpForDocument(document);
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
