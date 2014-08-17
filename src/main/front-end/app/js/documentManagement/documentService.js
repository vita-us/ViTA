(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  // Service that is responsible for dealing with documents
  vitaServices.factory('Document', ['$resource', function($resource) {
    return $resource('webapi/documents/:documentId', {}, {
      // method for retrieving a single document
      query: {
        method: 'GET'
      }
    });
  }]);

})(angular);