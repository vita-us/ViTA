(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the documents page
  vitaControllers.controller('DocumentsCtrl', ['$scope', 'Document', 'Page', 'FileUpload',
      function($scope, Document, Page, FileUpload) {
        Page.setUp('Documents', 1);

        Document.get(function(response) {
          $scope.documents = response.documents;
        });

        $scope.uploadSelectedFile = function() {
          if ($scope.file) {
            FileUpload.uploadFileToUrl($scope.file, '/documents', function(data, status) {
              // nothing to do: angular requests the latest document listing and
              // we poll them too
            }, function(data, status) {
              alert('Upload of ' + $scope.file.name + ' failed.');
            });
          } else {
            alert('Please select a document first.');
          }
        };

      }]);

})(angular);