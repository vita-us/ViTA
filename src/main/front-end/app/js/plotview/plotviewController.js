(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  vitaControllers.controller('PlotviewCtrl', ['$scope', 'DocumentParts', 'Page',
    function($scope, DocumentParts, Page) {

      Page.breadcrumbs = 'Plotview';
      Page.setUpForCurrentDocument();

      $scope.loaded = function() {
        return $('#plotview-wrapper').has('svg').length > 0;
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
