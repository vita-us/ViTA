(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  // Service that is responsible for dealing with document deriving
  vitaServices.factory('DocumentDerive', ['$resource', function($resource) {
    return $resource('webapi/documents/:documentId/derive', {}, {
      post: {
        method: 'POST'
      }
    });
  }]);

})(angular);
