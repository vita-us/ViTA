(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('documentUpload', [
      'FileUpload', 'AnalysisParameter',
      function(FileUpload, AnalysisParameter) {

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
          setupAnalysisParameters(scope);

          scope.uploading = false;
          scope.uploadSelectedFile = function() {
            // allow only a single upload simultaneously
            if (scope.uploading) {
              return;
            }

            if(!areParametersValid(scope)) {
              alert('Please correctly input all parameters.');
              return;
            }

            if (scope.file) {
              scope.uploading = true;

              FileUpload.uploadFileToUrl(scope.file, 'webapi/documents', getParameterToValueMap(scope),
                function() {
                // nothing to do: we poll the documents every X seconds
                resetUploadField();
                scope.uploading = false;
                scope.file = undefined;
              }, function() {
                scope.uploading = false;
                scope.file = undefined;
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

        function setupAnalysisParameters(scope) {
          AnalysisParameter.get({}, function(response) {
            scope.analysisParameters = response.parameters;
            scope.analysisParameters.forEach(function(parameter) {
              parameter.value = parameter.defaultValue;
            });
          });
        }

        function getParameterToValueMap(scope) {
          var parameterToValue = {};
          scope.analysisParameters.forEach(function(parameter) {
            parameterToValue[parameter.name] = parameter.value;
          });
          return parameterToValue;
        }

        function areParametersValid(scope) {
          var areValid = true;
          scope.analysisParameters.forEach(function(parameter) {
            if (angular.isUndefined(parameter.value)) {
              areValid = false;
            }
          });
          return areValid;
        }

        return directive;
      }]);

})(angular);
