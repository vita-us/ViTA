(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the documents page
  vitaControllers.controller('DocumentsCtrl', ['$scope', 'Document', 'Page', function($scope, Document, Page) {
    Page.setTitle('Documents');
    Page.setShowMenu(false);
    Page.setTab(1);
    
    $scope.documentsWrapper = Document.get(function() {
      $scope.documents = $scope.documentsWrapper.documents;
    });
  }]);

})(angular);