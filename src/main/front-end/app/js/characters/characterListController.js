(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the characters page
  vitaControllers.controller('CharacterListCtrl', ['$scope', 'Document', 'Page', 'Character', '$routeParams',
      function($scope, Document, Page, Character, $routeParams) {
        Character.get({
          documentId: $routeParams.documentId
        }, function(response) {
          $scope.characters = response.characters;
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
