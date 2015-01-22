(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  // Service that is responsible for dealing with document parameters
  vitaServices.factory('DocumentParameter', ['$resource', function($resource) {
    return $resource('webapi/documents/:documentId/parameters', {}, {
      get: {
        method: 'GET',
        cache: true
      }
    });
  }]);

})(angular);
