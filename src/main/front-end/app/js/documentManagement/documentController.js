(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the documents page
  vitaControllers.controller('DocumentsCtrl', ['$scope', 'Document', 'Page', 'FileUpload',
      '$interval', function($scope, Document, Page, FileUpload, $interval) {
        Page.setUp('Documents', 1);

        loadDocuments();
        var timerId = $interval(loadDocuments, 5000);

        $scope.uploadSelectedFile = function() {
          if ($scope.file) {
            FileUpload.uploadFileToUrl($scope.file, '/documents', function(data, status) {
              // nothing to do: we poll the documents every X seconds
            }, function(data, status) {
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

        $scope.$on('$destroy', function() {
          if (timerId) {
            $interval.cancel(timerId);
          }
        });
      }]);

})(angular);
