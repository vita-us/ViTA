(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  // Service that is responsible for dealing with wordclouds
  vitaServices.factory('Wordcloud', ['$resource', function($resource) {
    return $resource('webapi/documents/:documentId/wordcloud', {}, {
      get: {
        method: 'GET'
      }
    });
  }]);

})(angular);
