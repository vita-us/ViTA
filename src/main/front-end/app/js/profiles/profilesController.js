(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the profiles page
  vitaControllers.controller('ProfilesCtrl', ['$scope', 'Profile', 'Page', function($scope, Profile, Page) {
    Page.title = 'Profiles';
    Page.showMenu = false;
    Page.tab = 1;

      $scope.profiles = Profile.query();
      $scope.profile = Profile.get();
  }]);
})(angular);