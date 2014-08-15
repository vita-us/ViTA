(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  // Service that is responsible for dealing with documents
  vitaServices.factory('Profile', ['$resource', function($resource) {
    return $resource('/documents/:documentId/persons/:personId', {}, {
      // method for retrieving a specific profile
      get: {
        method: 'GET'
      },

      // method for retrieving all profiles
      query: {
        method: 'GET',
        isArray: true
      }
    });
  }]);
})(angular);