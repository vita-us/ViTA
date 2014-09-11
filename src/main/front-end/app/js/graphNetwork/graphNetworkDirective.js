(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('graphNetwork', [function() {

    var directive = {
      replace: false,
      restrict: 'EA',
      scope: {
        entities: '@'
      },
      link: function(scope, element) {
        var container = d3.select(element[0]);
      }
    };

    return directive;
  }]);

})(angular);
