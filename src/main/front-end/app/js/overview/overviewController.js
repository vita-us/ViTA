(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the overview page
  vitaControllers.controller('OverviewCtrl', ['$scope', 'Document', 'Page', '$routeParams',
      'AnalysisProgress', '$interval', 'Person', 'CssClass',
      function($scope, Document, Page, $routeParams, AnalysisProgress, $interval, Person, CssClass) {

        // Provide the service for direct usage in the scope
        $scope.CssClass = CssClass;

        Person.get({
          documentId: $routeParams.documentId
        }, function(response) {
          $scope.persons = response.persons;
        });

        Document.get({
          documentId: $routeParams.documentId
        }, function(document) {
          $scope.document = document;
          Page.breadcrumbs = 'Overview';
          Page.setUpForDocument(document);
        });

        loadAnalysisProgress();
        // Load the analysis progress repeatedly
        var timerToken = $interval(function() {
          loadAnalysisProgress();
        }, 5000);

        function loadAnalysisProgress() {
          AnalysisProgress.get({
            documentId: $routeParams.documentId
          }, function(progress) {
            $scope.progress = progress;
          });
        }

        $scope.$on('$destroy', function() {
          if (timerToken) {
            $interval.cancel(timerToken);
          }
        });

      }]);

})(angular);
