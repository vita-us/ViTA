(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the wordcloud page
  vitaControllers.controller('WordcloudCtrl', ['$scope', 'Wordcloud', 'Page', '$routeParams',
      function($scope, Wordcloud, Page, $routeParams) {

        Wordcloud.get({
          documentId: $routeParams.documentId
        }, function(response) {
          $scope.wordcloud = response.items;
        });
      }]);

})(angular);