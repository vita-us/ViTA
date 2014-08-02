(function(angular) {
  'use strict';

 var vitaServices = angular.module('vitaServices', ['ngResource']);

 // Service that is responsible for dealing with documents
 vitaServices.factory('Document', ['$resource',
  function($resource){
    return $resource('test_data/documents.json', {}, {
      query: {method:'GET'}
    });
  }]);

})(angular);