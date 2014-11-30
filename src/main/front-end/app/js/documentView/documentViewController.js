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
          $scope.selectedOccurrenceIndex = 0;
          $scope.resultCount = $scope.occurrences.length;
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
            $scope.selectedOccurrenceIndex = 0;
          });
        };

        $scope.reset = function() {
          $scope.resultCount = -1;
          $scope.occurrences = [];
          $scope.entities = [];
        };

        $scope.down = function() {
          $scope.selectedOccurrenceIndex = angular.isUndefined($scope.selectedOccurrenceIndex) ? 0
                  : $scope.selectedOccurrenceIndex;
          $scope.selectedOccurrenceIndex += 1;
          $scope.selectedOccurrenceIndex %= $scope.occurrences.length;
        }
        $scope.up = function() {
          $scope.selectedOccurrenceIndex = angular.isUndefined($scope.selectedOccurrenceIndex) ? 0
                  : $scope.selectedOccurrenceIndex;
          $scope.selectedOccurrenceIndex -= 1;
          $scope.selectedOccurrenceIndex = $scope.selectedOccurrenceIndex < 0
                  ? $scope.occurrences.length - 1 : $scope.selectedOccurrenceIndex;
        }
      }]);

})(angular);
