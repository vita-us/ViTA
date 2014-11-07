(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  vitaControllers.controller('GraphNetworkCtrl', ['$scope', '$routeParams', 'TestData', 'Document',
      'Page', function($scope, $routeParams, TestData, Document, Page) {
        Document.get({
          documentId: $routeParams.documentId
        }, function(document) {
          Page.breadcrumbs = 'Graph-Network';
          Page.setUpForDocument(document);
        });

        // TODO replace when implementing the graph network page
        $scope.entities = TestData.graphNetworkEntities;

        // Set a custom graph width
        $scope.graphWidth = $("#graph-network-wrapper").width();

        // Set a custom graph height like this
        $scope.graphHeight = $(window).height() * 0.7;
      }]);

})(angular);
