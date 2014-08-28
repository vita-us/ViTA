(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the overview page
  vitaControllers.controller('OverviewCtrl', ['$scope', 'Document', 'Page',
      function($scope, Document, Page) {
        // when rest-api exists instead documentId: $routeParams.documentId
        Document.get({
          documentId: 'single-document'
        }, function(document) {
          $scope.document = document;
          Page.breadcrumbs = 'Overview';
          Page.setUpForDocument(document);
        });
      }]);

})(angular);