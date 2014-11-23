(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  vitaServices.service('DocumentViewSender', ['SharedWorkerFactory',
                                              'DocumentViewWorkerConstants',
                                              function(workerFactory, constants) {
    var worker = workerFactory.create(), onReceiveCallback, onDocumentIdRequestCallback;

    // Register this port at the shared worker
    worker.port.start();
    worker.port.postMessage({
      sender: constants.APP,
      type: constants.REGISTER
    });

    worker.port.addEventListener('message', function(event) {
      var message = event.data;

      if (onReceiveCallback instanceof Function) {
        onReceiveCallback(message);
      }
      if (onDocumentIdRequestCallback instanceof Function &&
          message.type === constants.DOCUMENT_ID_REQUEST) {
        onDocumentIdRequestCallback(message);
      }
    }, false);

    function sendMessage(type, message) {
      worker.port.postMessage({
        sender: constants.APP,
        type: type || 'none',
        message: message
      });
    }

    this.onReceive = function(callback) {
      onReceiveCallback = callback;
    };

    this.onDocumentIdRequest = function(documentIdRequestCallback) {
      onDocumentIdRequestCallback = documentIdRequestCallback;
    };

    this.sendDocumentId = function(documentId) {
      sendMessage(constants.DOCUMENT_ID, documentId);
    };

    this.sendOccurrences = function(occurrences) {
      sendMessage(constants.OCCURRENCES, occurrences);
    };

    this.sendEntities = function(entities) {
      sendMessage(constants.ENTITIES, entities);
    };

  }]);

})(angular);
