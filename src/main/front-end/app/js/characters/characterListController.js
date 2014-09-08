(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the characters page
  vitaControllers.controller('CharacterListCtrl', ['$scope', 'Character', 'Page', '$routeParams',
      function($scope, Character, Page, $routeParams) {
        Page.title = 'Characters';
        Page.showMenu = true;
        Page.tab = 1;

        Character.get({
          documentId: $routeParams.documentId
        }, function(response) {
          $scope.characters = response.characters;
        });
      }]);
})(angular);
