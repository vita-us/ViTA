(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  vitaServices.factory('PlotviewService', ['$resource', function($resource) {
    return $resource('webapi/documents/:documentId/plotview', {}, {
      get: {
        method: 'GET',
        cache: true
      }
    });
  }]);

})(angular);
