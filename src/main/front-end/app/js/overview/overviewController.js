(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the overview page
  vitaControllers.controller('OverviewCtrl',
    ['$scope', 'Document', 'Page', '$routeParams', 'AnalysisProgress', '$interval', 'Person', 'CssClass',
      function($scope, Document, Page, $routeParams, AnalysisProgress, $interval, Person, CssClass) {

        // Provide the service for direct usage in the scope
        $scope.CssClass = CssClass;
        $scope.document = {};
        $scope.persons = [];

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
        }, 1000);

        function loadAnalysisProgress() {
          AnalysisProgress.get({
            documentId: $routeParams.documentId
          }, function(progress) {
            $scope.progress = progress;
          });
        }

        $scope.prepareAttributeForView = function(attribute) {
          return attribute != null ? attribute : '-';
        };

        $scope.$on('$destroy', function() {
          if (timerToken) {
            $interval.cancel(timerToken);
          }
        });

        $scope.metadataLoaded = function() {
          return !($scope.document.metadata == null);
        };

        $scope.metricsLoaded = function() {
          return !($scope.document.metrics == null);
        };

        $scope.charactersLoaded = function() {
          return $scope.persons.length != 0;
        };
      }]);

})(angular);
