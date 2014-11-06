(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  // Service that is responsible for dealing with fingerprints
  vitaServices.factory('Fingerprint', ['$resource', function($resource) {
    return $resource('/documents/:documentId/entities/fingerprints', {}, {
      get: {
        method: 'GET'
      }
    });
  }]);

})(angular);