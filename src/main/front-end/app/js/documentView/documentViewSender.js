(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  vitaServices.service('DocumentViewSender', ['SharedWorkerFactory', function(workerFactory) {
    var worker = workerFactory.create(), onReceiveCallback, onDocumentIdRequestCallback;

    // Register this port at the shared worker
    worker.port.start();
    worker.port.postMessage({
      sender: 'APP',
      type: 'REGISTER'
    });

    worker.port.addEventListener('message', function(event) {
      var message = event.data;

      if (onReceiveCallback instanceof Function) {
        onReceiveCallback(message);
      }
      if (onDocumentIdRequestCallback instanceof Function && message.type === 'DOCUMENTIDREQUEST') {
        onDocumentIdRequestCallback(message);
      }
    }, false);

    function sendMessage(type, message) {
      worker.port.postMessage({
        sender: 'APP',
        type: type || 'none',
        message: message
      });
    }

    this.onReceive = function(callback) {
      onReceiveCallback = callback;
    };

    this.onDocumentIdRequest = function(documentIdCallback) {
      onDocumentIdRequestCallback = documentIdCallback;
    };

    this.sendDocumentId = function(documentId) {
      sendMessage('DOCUMENTID', documentId);
    };

    this.sendOccurrences = function(occurrences) {
      sendMessage('OCCURRENCES', occurrences);
    };

  }]);

})(angular);
