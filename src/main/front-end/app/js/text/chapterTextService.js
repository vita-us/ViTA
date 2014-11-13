(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  vitaServices.factory('ChapterText', ['$resource', function($resource) {
    return $resource('/documents/:documentId/chapters/:chapterId', {}, {
      get: {
        method: 'GET'
      }
    });
  }]);

})(angular);
