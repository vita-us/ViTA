(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the tutorial page
  vitaControllers.controller('TutorialCtrl', ['$scope', 'Page', function($scope, Page) {
    Page.title = 'Tutorial';
    Page.showMenu = false;
    Page.tab = 3;
  }]);

})(angular);