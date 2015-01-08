(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  // Service that is responsible for dealing with documents
  vitaServices.factory('Document', ['$resource', function($resource) {
    return $resource('webapi/documents/:documentId', {
      documentId: '@documentId'
    }, {
      get: {
        method: 'GET'
      },
      rename: {
        method: 'PUT'
      }
      // delete method is implicitly declared
    });
  }]);

})(angular);
