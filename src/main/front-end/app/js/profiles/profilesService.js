(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  // Service that is responsible for dealing with profiles
  vitaServices.factory('Profiles', ['$resource', function($resource) {
    return $resource('test_data/:documentId/persons/', {}, {
      // method for retrieving a single profile
      get: {
        method: 'GET',
        params: {
          documentId: 'documents',
          personId: '1'
        }
      },
      
      // Custom method for retrieving all profiles
      query: {
        method: 'GET',
        params: {
          documentId: 'documents',
          offset: '1'
        }
      }
    });
  }]);

})(angular);