(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  // Service that is responsible for dealing with analysis-parameters
  vitaServices.factory('AnalysisParameter', ['$resource', function($resource) {
    return $resource('webapi/analysis-parameters', {}, {
      get: {
        method: 'GET',
        cache: true
      }
    });
  }]);
})(angular);
