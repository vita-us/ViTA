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

        Wordcloud.get({
          documentId: $routeParams.documentId
        }, function(response) {
          $scope.wordcloud = response.items;
        });
      }]);

})(angular);