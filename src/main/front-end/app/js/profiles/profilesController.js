(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the profiles page
  vitaControllers.controller('ProfilesCtrl', ['$scope', 'Profile', 'Page', '$routeParams',
      function($scope, Profile, Page, $routeParams) {
        Page.title = 'Profiles';
        Page.showMenu = true;
        Page.tab = 1;

        $scope.profiles = Profile.get({
          documentId: $routeParams.documentId
        });
      }]);
})(angular);
