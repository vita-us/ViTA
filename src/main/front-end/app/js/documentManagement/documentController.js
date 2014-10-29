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
              // TODO on success
              console.log(data, status);
            }, function(data, status) {
              // TODO error handling
              console.log(data, status);
            });
          } else {
            alert("Please select a document first.");
          }
        };

      }]);

})(angular);