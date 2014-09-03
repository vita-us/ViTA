(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the place page
  vitaControllers.controller('PlaceCtrl', ['$scope', 'Place', '$routeParams',
      function($scope, Place, $routeParams) {
        $scope.place = Place.get({
          documentId: $routeParams.documentId,
          placeId: $routeParams.placeId
        });
      }]);

})(angular);