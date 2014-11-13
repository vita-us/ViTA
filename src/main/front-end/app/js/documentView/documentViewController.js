(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  vitaControllers.controller('DocumentViewCtrl', ['$scope', 'DocumentViewReceiver', 'Document',
      'DocumentParts', function($scope, DocumentViewReceiver, Document, DocumentParts) {

        DocumentViewReceiver.onDocumentId(function(messageData) {
          var documentId = messageData.message;

          Document.get({
            documentId: documentId
          }, function(document) {
            $scope.document = document;
            requestParts(document);
          });
        });

        DocumentViewReceiver.onOccurrences(function(messageData) {
          $scope.occurrences = messageData.message;
          $scope.$digest();
        });

        DocumentViewReceiver.requestDocumentId();

        function requestParts(document) {
          DocumentParts.get({
            documentId: document.id
          }, function(partData) {
            $scope.parts = partData.parts;
          });
        }
      }]);

})(angular);
