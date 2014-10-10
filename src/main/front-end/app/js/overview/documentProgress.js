(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  vitaServices.factory('Progress', ['$resource', function($resource) {
    return $resource('/documents/:documentId/progress', {}, {
      get: {
        method: 'GET'
      }
    });
  }]);
})(angular);
