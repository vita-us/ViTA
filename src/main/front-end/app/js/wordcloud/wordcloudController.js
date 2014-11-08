(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the wordcloud page
  vitaControllers.controller('WordcloudCtrl', ['$scope', 'Wordcloud', 'Page', '$routeParams',
      function($scope, Wordcloud, Page, $routeParams) {

        $scope.activeWordcloud = 'global';
        $scope.update=  function(person) {
          $scope.activeWordcloud = person.id;
          Wordcloud.get({
            documentId: $routeParams.documentId,
            entityId: person.id
          }, function(response) {
            $scope.wordcloud = response.items;
          });
        };

        $scope.global = function() {
          $scope.activeWordcloud = 'global';
          Wordcloud.get({
            documentId: $routeParams.documentId
          }, function(response) {
            $scope.wordcloud = response.items;
          });
        };

        Wordcloud.get({
          documentId: $routeParams.documentId
        }, function(response) {
          $scope.wordcloud = response.items;
        });
      }]);
})(angular);
