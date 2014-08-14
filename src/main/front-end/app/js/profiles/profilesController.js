(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the profiles page
  vitaControllers.controller('ProfilesCtrl', ['$scope', 'Profiles', 'Page', function($scope, Profiles, Page) {
    Page.title = 'Profiles';
    Page.showMenu = false;
    Page.tab = 1;
  }]);

})(angular);