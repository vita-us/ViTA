(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  vitaServices.service('SharedWorkerFactory', [function() {
    this.create = function() {
      return new SharedWorker("js/shared-worker.js");
    };
  }]);

})(angular);
