(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  // Service that is responsible for dealing with profiles
  vitaServices.factory('Profiles', ['$resource', function($resource) {
    return $resource('test_data/:documentId/persons/', {}, {
      // method for retrieving a single profile
      get: {
        method: 'GET'
      },
      
      // Custom method for retrieving all profiles
      query: {
        method: 'GET',
        isArray: true
      }
    });
  }]);

})(angular);