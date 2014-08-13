(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the about page
  vitaControllers.controller('AboutCtrl', ['$scope', 'Page', function($scope, Page) {
    Page.title = 'About ViTA';
    Page.showMenu = false;
    Page.tab = 4;
  }]);

})(angular);