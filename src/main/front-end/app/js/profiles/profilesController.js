(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the profiles page
  vitaControllers.controller('ProfilesCtrl', ['$scope', 'Profile', 'Page', function($scope, Profile, Page) {
    Page.title = 'Profiles';
    Page.showMenu = false;
    Page.tab = 1;

    $scope.documentId = 'doc13a';
    $scope.personId = 'person10Bert';
    $scope.personOffset = '1';

    $scope.profiles = Profile.query(
      {
        documentId: $scope.documentId,
        offset: $scope.personOffset
      });

    $scope.profile = Profile.get(
      {
        documentId: $scope.documentId,
        personId: $scope.personId
      });
  }]);
})(angular);