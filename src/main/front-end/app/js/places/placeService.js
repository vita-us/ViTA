(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  // Service that is responsible for dealing with places
  vitaServices.factory('Place', ['$resource', function($resource) {
    return $resource('/documents/:documentId/places/:placeId', {}, {
      get: {
        method: 'GET'
      }
    });
  }]);

})(angular);