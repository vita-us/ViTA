(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the profiles page
  vitaControllers.controller('ProfilesCtrl', ['$scope', 'Profile', 'Page', function($scope, Profile, Page) {
    Page.title = 'Profiles';
    Page.showMenu = true;
    Page.tab = 1;

    $scope.documentId = 'doc13a';

    $scope.profiles = Profile.get({
      documentId: $scope.documentId
    });
  }]);
})(angular);