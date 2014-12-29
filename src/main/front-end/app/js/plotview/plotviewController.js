(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  vitaControllers.controller('PlotviewCtrl', ['$scope', '$routeParams', 'DocumentParts',
    'Document', 'Page',
    function($scope, $routeParams, DocumentParts, Document, Page) {

      Document.get({
        documentId: $routeParams.documentId
      }, function(document) {
        Page.breadcrumbs = 'Plotview';
        Page.setUpForDocument(document);
      });

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
