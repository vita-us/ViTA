(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  vitaControllers.controller('GraphNetworkCtrl', [
      '$scope',
      '$routeParams',
      'TestData',
      function($scope, $routeParams, TestData) {
        // TODO replace when implementing the graph network page
        $scope.entities = TestData.graphNetworkEntities;

        var sliderMin = 0, sliderMax = 100;

        $('#slider-range').slider({
          range: true,
          min: sliderMin,
          max: sliderMax,
          values: [sliderMin, sliderMax],
          slide: function(event, ui) {
            var start = ui.values[0], end = ui.values[1];

            setSliderLabel(start, end);

            $scope.rangeStart = start / 100;
            $scope.rangeEnd = end / 100;
            $scope.$apply();
          }
        });

        setSliderLabel(sliderMin, sliderMax);

        function setSliderLabel(start, end) {
          $('#amount').val(start + ' - ' + end);
        }

        $scope.loadGraphNetwork = function(person) {
          alert(person.id);
        };
      }]);

})(angular);
