(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  vitaServices.service('DocumentViewReceiver', ['SharedWorkerFactory', function(workerFactory) {
    var worker = workerFactory.create(), onReceiveCallback, onOccurrenceCallback;

    // Register this port at the shared worker
    worker.port.start();
    worker.port.postMessage({
      sender: 'DOCUMENTVIEW',
      type: 'REGISTER'
    });

    worker.port.addEventListener('message', function(event) {
      var message = event.data;

      if (onReceiveCallback instanceof Function) {
        onReceiveCallback(message);
      }
      if (onOccurrenceCallback instanceof Function && message.type === 'OCCURRENCES') {
        onOccurrenceCallback(message);
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

    this.onOccurrences = function(occurrenceCallback) {
      onOccurrenceCallback = occurrenceCallback;
    };

  }]);

})(angular);
