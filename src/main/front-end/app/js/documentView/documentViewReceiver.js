(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  vitaServices.service('DocumentViewReceiver', ['SharedWorkerFactory', function(workerFactory) {
    var worker = workerFactory.create(), onReceiveCallback;

    // Register this port at the shared worker
    worker.port.start();
    worker.port.postMessage({
      sender: 'DOCUMENTVIEW',
      type: 'REGISTER'
    });

    worker.port.addEventListener('message', function(event) {
      if (onReceiveCallback instanceof Function) {
        onReceiveCallback(event.data);
      }
    }, false);

    function sendMessage(type, message) {
      worker.port.postMessage({
        sender: 'DOCUMENTVIEW',
        type: type || 'none',
        message: message
      });
    }

    this.onReceive = function(callback) {
      onReceiveCallback = callback;
    };

  }]);

})(angular);
