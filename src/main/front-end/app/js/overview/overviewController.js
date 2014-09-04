(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the overview page
  vitaControllers.controller('OverviewCtrl', ['$scope', 'Document', '$routeParams',
      function($scope, Document, $routeParams) {

        $scope.document = Document.get({
          documentId: $routeParams.documentId
        });
      }]);

})(angular);