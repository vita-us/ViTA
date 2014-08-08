(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the tutorial page
  vitaControllers.controller('TutorialCtrl', ['$scope', 'Page', function($scope, Page) {
    Page.setTitle('Tutorial');
    Page.setShowMenu(false);
  }]);

})(angular);