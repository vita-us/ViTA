(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  vitaServices.service('DocumentViewSender', ['SharedWorkerFactory', function(workerFactory) {
    var worker = workerFactory.create(), onReceiveCallback;

    // Register this port at the shared worker
    worker.port.start();
    worker.port.postMessage({
      sender: "APP",
      type: "REGISTER"
    });

    worker.port.addEventListener("message", function(event) {
      if (onReceiveCallback instanceof Function) {
        onReceiveCallback(event.data);
      }
    }, false);

    this.sendTestMessage = function() {
      worker.port.postMessage({
        sender: "APP",
        type: "none",
        message: "hello documentview"
      });
    };

    this.onReceive = function(callback) {
      onReceiveCallback = callback;
    };

  }]);

})(angular);
