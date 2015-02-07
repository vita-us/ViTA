(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  // Service that is responsible for dealing with entities
  vitaServices.factory('Entity', ['$resource', '$cacheFactory', function($resource, $cacheFactory) {
    var cache = $cacheFactory('entity');
    return $resource('webapi/documents/:documentId/entities/:entityId', {}, {
      get: {
        method: 'GET',
        cache: cache
      }
    });
  }]);
})(angular);
