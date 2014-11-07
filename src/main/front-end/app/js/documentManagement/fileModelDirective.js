(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('fileModel', ['$parse', function($parse) {

    var directive = {
      restrict: 'A',
      link: function(scope, element, attrs) {
        // http://uncorkedstudios.com/blog/multipartformdata-file-upload-with-angularjs
        var model = $parse(attrs.fileModel);
        var modelSetter = model.assign;

        element.bind('change', function() {
          scope.$apply(function() {
            modelSetter(scope, element[0].files[0]);
          });
        });
      }
    };
    return directive;
  }]);

})(angular);
