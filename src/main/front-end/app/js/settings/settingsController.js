(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the settings page
  vitaControllers.controller('SettingsCtrl', ['$scope', 'Page', function($scope, Page) {
    Page.setUp('Settings', 2);
  }]);

})(angular);