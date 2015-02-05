(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the tutorial page
  vitaControllers.controller('TutorialCtrl', ['$scope', 'Page', function($scope, Page) {
    Page.setUp('Tutorial', 2);
  }]);

})(angular);
