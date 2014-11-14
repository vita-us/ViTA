(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  vitaServices.factory('DocumentSearch', ['$resource', function($resource) {
    return $resource('/documents/:documentId/search', {}, {
      search: {
        method: 'GET'
      }
    });
  }]);

})(angular);
