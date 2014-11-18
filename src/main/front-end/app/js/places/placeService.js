(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  // Service that is responsible for dealing with places
  vitaServices.factory('Place', ['$resource', function($resource) {
    return $resource('webapi/documents/:documentId/places/:placeId', {}, {
      get: {
        method: 'GET'
      }
    });
  }]);

})(angular);
