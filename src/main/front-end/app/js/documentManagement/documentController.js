(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the documents page
  vitaControllers.controller('DocumentsCtrl', ['$scope', 'Document', 'Page',
      function($scope, Document, Page) {
        Page.setUp('Documents', 1);

        Document.get(function(response) {
          $scope.documents = response.documents;
        });
      }]);

})(angular);