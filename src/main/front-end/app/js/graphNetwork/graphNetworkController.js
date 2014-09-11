(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  vitaControllers.controller('GraphNetworkCtrl', ['$scope', '$routeParams',
      function($scope, $routeParams) {
        $scope.entities = ['123', '513'];
      }]);

})(angular);
