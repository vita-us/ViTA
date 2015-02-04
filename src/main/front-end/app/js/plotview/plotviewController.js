(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  vitaControllers.controller('PlotviewCtrl', ['$scope', '$routeParams', 'DocumentParts', 'Page',
    function($scope, $routeParams, DocumentParts, Page) {

      Page.breadcrumbs = 'Plotview';
      Page.setUpForDocument($routeParams.documentId);

      $scope.loading = function() {
        return $('#plotview-wrapper').has('svg').length != 1;
      };

      setPlotviewDimensions();
      $(window).resize(function() {
        setPlotviewDimensions();
        $scope.$apply();
      });

      function setPlotviewDimensions() {
        $scope.plotviewWidth = $('#plotview-wrapper').width();
        $scope.plotviewHeight = $(window).height() * 0.7;
      }
    }]);

})(angular);
