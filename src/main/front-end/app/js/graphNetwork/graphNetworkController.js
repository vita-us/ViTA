(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  vitaControllers.controller('GraphNetworkCtrl', ['$scope', '$routeParams', 'TestData',
      function($scope, $routeParams, TestData) {
        // TODO replace when implementing the graph network page
        $scope.entities = TestData.graphNetworkEntities;

        var test = function() {
          $( "#slider-range" ).slider({
            range: true,
            min: 0,
            max: 100,
            values: [ 0, 100 ],
            slide: function( event, ui ) {
              $( "#amount" ).val( ui.values[ 0 ] + " - " + ui.values[ 1 ] );
            }
          });
          $( "#amount" ).val( $( "#slider-range" ).slider( "values", 0 ) +
               " - " + $( "#slider-range" ).slider( "values", 1 ) );
        };
	test();
      }]);

})(angular);
