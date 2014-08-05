(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers', []);

  // Controller responsible for the documents page
  vitaControllers.controller('DocumentsCtrl', ['$scope', 'Document', function($scope, Document) {

    $scope.documentsWrapper = Document.get(function() {
      $scope.documents = $scope.documentsWrapper.documents;
    });
  }]);

})(angular);