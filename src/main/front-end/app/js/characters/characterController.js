(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the characters page
  vitaControllers.controller('CharacterCtrl', ['$scope', 'Character', 'Page', '$routeParams',
      function($scope, Character, Page, $routeParams) {
        Page.title = 'Character';
        Page.showMenu = true;
        Page.tab = 1;

        $scope.character = Character.get({
          documentId: $routeParams.documentId,
          characterId: $routeParams.characterId
        });
      }]);
})(angular);
