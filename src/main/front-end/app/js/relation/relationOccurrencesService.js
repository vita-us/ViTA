(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  // Service that is responsible for dealing with relation occurrences
  vitaServices.factory('RelationOccurrences', ['$resource', function($resource) {
    return $resource('/documents/:documentId/entitities/relations/occurrences', {}, {
      get: {
        method: 'GET'
      }
    });
  }]);
})(angular);
