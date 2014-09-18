(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the person page
  vitaControllers.controller('PersonCtrl', ['$scope', 'Document', 'Page', 'Person', '$routeParams',
      function($scope, Document, Page, Person, $routeParams) {
        var personName = '';
    
        Person.get({
          documentId: $routeParams.documentId,
          personId: $routeParams.personId
        }, function(singlePerson) {
          $scope.person = singlePerson;
          personName = singlePerson.displayName;
        });
        
        Document.get({
          documentId: $routeParams.documentId
        }, function(document) {
          $scope.document = document;
          Page.breadcrumbs = 'Characters > ' + personName;
          Page.setUpForDocument(document);
        });
      }]);
})(angular);
