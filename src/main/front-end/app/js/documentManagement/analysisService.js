(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  vitaServices.service('Analysis', ['$resource', function($resource) {

    var resource = $resource('/documents/:documentId/analysis/:method', {
      documentId: '@documentId',
      method: '@method'
    }, {
      put: {
        method: 'PUT'
      }
    });

    this.stop = function(documentId) {
      resource.put({
        documentId: documentId,
        method: 'stop'
      });
    };

    this.restart = function(documentId) {
      resource.put({
        documentId: documentId,
        method: 'restart'
      });
    };

  }]);

})(angular);
