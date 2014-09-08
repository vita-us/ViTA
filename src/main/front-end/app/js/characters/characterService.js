(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  // Service that is responsible for dealing with characters
  vitaServices.factory('Character', ['$resource', function($resource) {
    return $resource('/documents/:documentId/characters/:characterId', {}, {
      get: {
        method: 'GET'
      }
    });
  }]);
})(angular);
