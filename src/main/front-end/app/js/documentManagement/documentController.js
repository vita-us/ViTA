(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the documents page
  vitaControllers.controller('DocumentsCtrl', [
      '$scope',
      'Document',
      'Page',
      'FileUpload',
      '$interval',
      'ChapterText',
      function($scope, Document, Page, FileUpload, $interval, ChapterText) {
        Page.setUp('Documents', 1);

        var allowedExtensions = ['.txt', '.epub'];
        $scope.allowedExtensions = allowedExtensions.join(',');

        // Validate the file selection
        $scope.$watch('file', function() {
          if (!$scope.file) {
            return;
          }

          var isValid = false;

          for (var i = 0, l = allowedExtensions.length; i < l; i++) {
            var extension = allowedExtensions[i], name = $scope.file.name.toLowerCase();

            if (name.indexOf(extension, name.length - extension.length) !== -1) {
              isValid = true;
              break;
            }
          }

          if (!isValid) {
            alert('Invalid file selection. Only the following extensions are allowed: '
                    + $scope.allowedExtensions);
            resetUploadField();
          }
        });

        $scope.uploading = false;

        $scope.loadDocuments = function() {
          Document.get(function(response) {
            $scope.documents = response.documents;
          });
        };

        $scope.loadDocuments();
        var timerId = $interval($scope.loadDocuments, 5000);

        $scope.uploadSelectedFile = function() {
          // allow only a single upload simultaneously
          if ($scope.uploading) {
            return;
          }

          if ($scope.file) {
            $scope.uploading = true;

            FileUpload.uploadFileToUrl($scope.file, 'webapi/documents', function() {
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

        function resetUploadField() {
          document.getElementById('document-input').value = '';
        }

        $scope.isDocumentSelected = function(document) {
          return angular.equals($scope.selectedDocument, document);
        };

        $scope.updateSelection = function(selectedDocument) {
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
          }
        };

        $scope.$on('$destroy', function() {
          if (timerId) {
            $interval.cancel(timerId);
          }
        });
      }]);

})(angular);
