(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the documents page
  vitaControllers.controller('DocumentsCtrl', [
      '$scope',
      'Document',
      'Page',
      '$interval',
      function($scope, Document, Page, $interval) {
        Page.setUp('Documents', 1);

        $scope.loadDocuments = function() {
          Document.get(function(response) {
            $scope.documents = response.documents;

            /*
             * Update the selected document because the stored object might
             * be different from the object in the (reloaded) listing.
             */
            for (var i = 0, l = $scope.documents.length; i < l; i++) {
              var document = $scope.documents[i];

              if ($scope.isDocumentSelected(document)) {
                $scope.setSelectedDocument(document);
              }
            }
          });
        };

        $scope.loadDocuments();
        var timerId = $interval($scope.loadDocuments, 1000);

        $scope.isDocumentSelected = function(document) {
          if (!$scope.selectedDocument || !document) {
            return false;
          }

          return angular.equals($scope.selectedDocument.id, document.id);
        };

        $scope.setSelectedDocument = function(selectedDocument) {
          $scope.selectedDocument = selectedDocument;
        };

        $scope.renameDocument = function() {
          var document = $scope.selectedDocument;
          var newName = prompt('Please enter a new name for document "' + document.metadata.title
                  + '".');
          if (newName) {
            document.metadata.title = newName;
            Document.rename({
              documentId: document.id,
              name: newName
            }, function() {
              $scope.loadDocuments();
            });
          } else if (newName === '') {
            alert('The document name must not be empty!');
          }
        };

        $scope.$on('$destroy', function() {
          if (timerId) {
            $interval.cancel(timerId);
          }
        });
      }]);

})(angular);
