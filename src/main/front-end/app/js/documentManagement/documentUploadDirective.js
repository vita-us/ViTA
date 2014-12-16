(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('documentUpload', [
      'FileUpload',
      function(FileUpload) {

        var allowedExtensions = ['.txt', '.epub'];

        var directive = {
          restrict: 'A',
          scope: {},
          link: link,
          templateUrl: 'templates/documentupload.html'
        };

        function link(scope) {
          scope.allowedExtensionsString = allowedExtensions.join(',');

          setupFileValidation(scope);

          scope.uploading = false;
          scope.uploadSelectedFile = function() {
            // allow only a single upload simultaneously
            if (scope.uploading) {
              return;
            }

            if (scope.file) {
              scope.uploading = true;

              FileUpload.uploadFileToUrl(scope.file, 'webapi/documents', function() {
                // nothing to do: we poll the documents every X seconds
                resetUploadField();
                scope.uploading = false;
              }, function() {
                scope.uploading = false;
                alert('Upload of ' + scope.file.name + ' failed.');
              });
            } else {
              alert('Please select a document first.');
            }
          };
        }

        /**
         * Required, because e.g. firefox doesn't consider the accept HTML
         * attribute
         */
        function setupFileValidation(scope) {
          scope.$watch('file', function() {
            if (!scope.file) { return; }

            var isValid = false;

            for (var i = 0, l = allowedExtensions.length; i < l; i++) {
              var extension = allowedExtensions[i], name = scope.file.name.toLowerCase();

              if (name.indexOf(extension, name.length - extension.length) !== -1) {
                isValid = true;
                break;
              }
            }

            if (!isValid) {
              alert('Invalid file selection. Only the following extensions are allowed: '
                      + scope.allowedExtensionsString);
              resetUploadField();
            }
          });
        }

        function resetUploadField() {
          document.getElementById('document-input').value = '';
        }

        return directive;
      }]);

})(angular);
