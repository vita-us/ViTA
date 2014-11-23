(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  vitaControllers.controller('DocumentViewCtrl', ['$scope', 'DocumentViewReceiver', 'Document',
      'DocumentParts', 'DocumentSearch',
      function($scope, DocumentViewReceiver, Document, DocumentParts, DocumentSearch) {

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

        DocumentViewReceiver.onEntities(function(messageData) {
          $scope.entities = messageData.message;
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

        $scope.search = function() {
          DocumentSearch.search({
            documentId: $scope.document.id,
             query: $scope.query
          }, function(response) {
            var occurrences = response.occurrences;
            $scope.resultCount = occurrences.length;
            $scope.occurrences = occurrences;
          });
        };
      }]);

})(angular);
