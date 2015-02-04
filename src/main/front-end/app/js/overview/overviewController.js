(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the overview page
  vitaControllers.controller('OverviewCtrl', ['$scope', 'Page', '$routeParams', 'AnalysisProgress',
      '$interval', 'Person', 'CssClass',
      function($scope, Page, $routeParams, AnalysisProgress, $interval, Person, CssClass) {

        // Provide the service for direct usage in the scope
        $scope.CssClass = CssClass;

        loadPersons();

        Page.breadcrumbs = 'Overview';
        Page.setUpForDocument($routeParams.documentId);

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
            if (progress.persons.isReady && !$scope.persons) {
              loadPersons();
            }
          });
        }

        function loadPersons() {
          Person.get({
            documentId: $routeParams.documentId
          }, function(response) {
            $scope.persons = response.persons;
          });
        }

        $scope.prepareAttributeForView = function(attribute) {
          return attribute ? attribute : "-";
        };

        $scope.$on('$destroy', function() {
          if (timerToken) {
            $interval.cancel(timerToken);
          }
        });

      }]);

})(angular);
