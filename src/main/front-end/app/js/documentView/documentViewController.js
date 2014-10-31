(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  vitaControllers.controller('DocumentViewCtrl', ['$scope', 'DocumentViewReceiver', 'Document',
      function($scope, DocumentViewReceiver, Document) {

        DocumentViewReceiver.onDocumentId(function(messageData) {
          var documentId = messageData.message;

          Document.get({
            documentId: documentId
          }, function(document) {
            $scope.document = document;
          });
        });

        DocumentViewReceiver.requestDocumentId();
      }]);

})(angular);
