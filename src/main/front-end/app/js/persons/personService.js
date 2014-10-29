(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  // Service that is responsible for dealing with persons
  vitaServices.factory('Person', ['$resource', function($resource) {
    return $resource('./webapi/documents/:documentId/persons/:personId', {}, {
      get: {
        method: 'GET'
      }
    });
  }]);
})(angular);
