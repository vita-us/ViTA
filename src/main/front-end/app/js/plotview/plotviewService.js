(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  vitaServices.factory('PlotviewService', ['$resource', '$cacheFactory', function($resource, $cacheFactory) {
    var cache = $cacheFactory('plotview');
    return $resource('webapi/documents/:documentId/plotview', {}, {
      get: {
        method: 'GET',
        cache: cache
      }
    });
  }]);

})(angular);
