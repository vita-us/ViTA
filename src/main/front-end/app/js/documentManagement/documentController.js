(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the documents page
  vitaControllers.controller('DocumentsCtrl', ['$scope', 'Document', 'Page', 'FileUpload',
      '$interval', 'ChapterText',
      function($scope, Document, Page, FileUpload, $interval, ChapterText) {
        Page.setUp('Documents', 1);

        $scope.uploading = false;

        loadDocuments();
        var timerId = $interval(loadDocuments, 5000);

        $scope.uploadSelectedFile = function() {
          // allow only a single upload simultaneously
          if ($scope.uploading) {
            return;
          }

          if ($scope.file) {
            $scope.uploading = true;

            FileUpload.uploadFileToUrl($scope.file, '/documents', function() {
              // nothing to do: we poll the documents every X seconds
              resetUploadField();
              $scope.uploading = false;
            }, function() {
              $scope.uploading = false;
              alert('Upload of ' + $scope.file.name + ' failed.');
            });
          } else {
            alert('Please select a document first.');
          }
        };

        function loadDocuments() {
          Document.get(function(response) {
            $scope.documents = response.documents;
          });
        }

        function resetUploadField() {
          document.getElementById('document-input').value = '';
        }

        $scope.isDocumentSelected = function(document) {
          return angular.equals($scope.selectedDocument, document);
        };

        $scope.updateSelection = function(selectedDocument) {
          $scope.selectedDocument = selectedDocument;
        };

        $scope.$on('$destroy', function() {
          if (timerId) {
            $interval.cancel(timerId);
          }
        });
      }]);

})(angular);
