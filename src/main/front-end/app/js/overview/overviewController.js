(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the overview page
  vitaControllers.controller('OverviewCtrl', ['$scope', 'Document', 'Page', '$routeParams',
      'Progress', function($scope, Document, Page, $routeParams, Progress) {

        Document.get({
          documentId: $routeParams.documentId
        }, function(document) {
          $scope.document = document;
          Page.breadcrumbs = 'Overview';
          Page.setUpForDocument(document);
        });

        Progress.get({
          documentId: $routeParams.documentId
        }, function(progress) {
          $scope.progress = progress;
        });
      }]);

})(angular);