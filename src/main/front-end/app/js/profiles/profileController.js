(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the profiles page
  vitaControllers.controller('ProfileCtrl', ['$scope', 'Profile', 'Page', '$routeParams',
      function($scope, Profile, Page, $routeParams) {
        Page.title = 'Profile';
        Page.showMenu = true;
        Page.tab = 1;

        $scope.profile = Profile.get({
          documentId: $routeParams.documentId,
          personId: $routeParams.personId
        });
      }]);
})(angular);
