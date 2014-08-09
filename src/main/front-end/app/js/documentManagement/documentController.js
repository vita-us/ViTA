(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the documents page
  vitaControllers.controller('DocumentsCtrl', ['$scope', 'Document', 'Page', function($scope, Document, Page) {
    Page.title = 'Documents';
    Page.showMenu = false;
    Page.tab = 1;
    
    $scope.documentsWrapper = Document.get(function() {
      $scope.documents = $scope.documentsWrapper.documents;
    });
  }]);

})(angular);