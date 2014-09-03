(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the places page
  vitaControllers.controller('PlaceListCtrl', ['$scope', 'Place', '$routeParams',
      function($scope, Place, $routeParams) {
        Place.get({
          documentId: $routeParams.documentId,
        }, function(placesWrapper) {
          $scope.places = placesWrapper.places;
        });
      }]);

})(angular);