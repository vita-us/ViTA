(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  vitaServices.factory('DocumentParts', ['$resource', function($resource) {
    return $resource('/documents/:documentId/parts', {}, {
      get: {
        method: 'GET'
      }
    });
  }]);

})(angular);
