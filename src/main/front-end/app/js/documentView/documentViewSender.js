(function(angular) {
  'use strict';

  var vitaServices = angular.module('vitaServices');

  vitaServices.service('DocumentViewSender', ['SharedWorkerFactory',
                                              'DocumentViewWorkerConstants',
                                              '$timeout',
                                              function(workerFactory, constants, $timeout) {
    var worker = workerFactory.create(), onReceiveCallback, onDocumentIdRequestCallback;

    var windowObject;

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

    this.open = function(onOpenCallback) {
      if (!windowObject || windowObject.closed) {
        windowObject = window.open('documentview.html', 'documentView',
                'width=0,height=0,left=0,alwaysRaised=yes');
        if (onOpenCallback instanceof Function) {
          $timeout(onOpenCallback, 1500);
        }
      } else {
        windowObject.focus();
        if (onOpenCallback instanceof Function) {
          onOpenCallback();
        }
      }
    };

  }]);

})(angular);
