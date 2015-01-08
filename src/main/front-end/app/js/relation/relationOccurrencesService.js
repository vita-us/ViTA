(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  // Service that is responsible for dealing with relation occurrences
  vitaServices.factory('RelationOccurrences', ['$resource', function($resource) {
    return $resource('webapi/documents/:documentId/entities/relations/occurrences', {}, {
      get: {
        method: 'GET',
        cache: true
      }
    });
  }]);
})(angular);
