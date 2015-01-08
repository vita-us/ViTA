(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  vitaServices.factory('ChapterText', ['$resource', function($resource) {
      return $resource('webapi/documents/:documentId/chapters/:chapterId', {}, {
        get: {
          method: 'GET',
          cache: true
        }
      });
    }]);

})(angular);
