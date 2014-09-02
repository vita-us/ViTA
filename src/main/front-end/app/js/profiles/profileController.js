(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the profiles page
  vitaControllers.controller('ProfileCtrl', ['$scope', 'Profile', 'Page',
      function($scope, Profile, Page) {
        Page.title = 'Profile';
        Page.showMenu = true;
        Page.tab = 1;

        $scope.documentId = 'doc13a';
        $scope.personId = 'person10Bert';

        $scope.profile = Profile.get({
          documentId: $scope.documentId,
          personId: $scope.personId
        });
      }]);
})(angular);