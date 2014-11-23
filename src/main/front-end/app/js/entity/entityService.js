(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  // Service that is responsible for dealing with entities
  vitaServices.factory('Entity', ['$resource', function($resource) {
    return $resource('webapi/documents/:documentId/entities/:entityId', {}, {
      get: {
        method: 'GET'
      }
    });
  }]);
})(angular);
