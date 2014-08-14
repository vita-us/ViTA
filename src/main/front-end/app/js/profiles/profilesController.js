(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the profiles page
  vitaControllers.controller('ProfilesCtrl', ['$scope', 'Profiles', 'Page', function($scope, Profiles, Page) {
    Page.title = 'Profiles';
    Page.showMenu = false;
    Page.tab = 1;

    $scope.initProfileList = function(documentID, profileOffset) {
      $scope.profiles = Profiles.query(
        {
          documentId: documentID || 'documents',
          offset: profileOffset || '1'
        });
    };

    $scope.initProfile = function(documentID, profileID) {
      $scope.profile = Profiles.get(
        {
          documentId: documentID || 'documents',
          personId: profileID || '0'
        });
    };
  }]);
})(angular);