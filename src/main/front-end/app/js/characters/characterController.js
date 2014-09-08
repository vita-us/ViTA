(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the characters page
  vitaControllers.controller('CharacterCtrl', ['$scope', 'Document', 'Page', 'Character', '$routeParams',
      function($scope, Document, Page, Character, $routeParams) {
        var charName = '';
    
        Character.get({
          documentId: $routeParams.documentId,
          characterId: $routeParams.characterId
        }, function(singleCharacter) {
          $scope.character = singleCharacter;
          charName = singleCharacter.displayName;
        });
        
        Document.get({
          documentId: $routeParams.documentId
        }, function(document) {
          $scope.document = document;
          Page.breadcrumbs = 'Characters > ' + charName;
          Page.setUpForDocument(document);
        });
      }]);
})(angular);
