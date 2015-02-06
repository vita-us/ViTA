(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  // Service that is responsible for dealing with wordclouds
  vitaServices.factory('Wordcloud', ['$resource', '$cacheFactory', function($resource, $cacheFactory) {
    var cache = $cacheFactory('wordcloud');
    return $resource('webapi/documents/:documentId/wordcloud', {}, {
      get: {
        method: 'GET',
        cache: cache
      }
    });
  }]);

})(angular);
