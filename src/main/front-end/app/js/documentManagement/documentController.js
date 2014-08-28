(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the documents page
  vitaControllers.controller('DocumentsCtrl', ['$scope', 'Document', 'Page', function($scope, Document, Page) {
    Page.setUp('Documents', 1);
    
    $scope.documentsWrapper = Document.getAll(function() {
      $scope.documents = $scope.documentsWrapper.documents;
    });
  }]);

})(angular);