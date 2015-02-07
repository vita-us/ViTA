(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the tutorial page
  vitaControllers.controller('TutorialCtrl', ['$scope', 'Page', '$location', '$anchorScroll',
    function($scope, Page, $location, $anchorScroll) {

      Page.setUp('Tutorial', 2);

      /**
       * Scrolls to ids with angular modules due to the hash parameter being used for routing.
       * @param id the id of the dom element
       */
      $scope.scrollTo = function(id) {
        $location.hash(id);
        $anchorScroll();
      }

    }]);

})(angular);
