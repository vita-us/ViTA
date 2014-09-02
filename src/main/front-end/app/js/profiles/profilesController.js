(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the profiles page
  vitaControllers.controller('ProfilesCtrl', ['$scope', 'Profile', 'Page', '$routeParams',
      function($scope, Profile, Page, $routeParams) {
        Page.title = 'Profiles';
        Page.showMenu = true;
        Page.tab = 1;

        Profile.get({
          documentId: $routeParams.documentId
        }, function(response) {
          $scope.profiles = response.persons;
        });
      }]);
})(angular);
