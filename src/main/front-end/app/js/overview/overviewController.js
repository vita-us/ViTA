(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the overview page
  vitaControllers.controller('OverviewCtrl', ['$scope', 'Document', function($scope, Document) {
    // when rest-api exists instead documentId: $routeParams.documentId
    $scope.document = Document.get({
      documentId: 'single-document'
    });
  }]);

})(angular);