(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  vitaServices.factory('EntityRelation', ['$resource', function($resource) {
    return $resource('webapi/documents/:documentId/entities/relations', {}, {
      get: {
        method: 'GET',
        cache: true
      }
    });
  }]);

})(angular);
