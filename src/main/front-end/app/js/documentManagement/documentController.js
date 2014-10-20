(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the documents page
  vitaControllers.controller('DocumentsCtrl', ['$scope', 'Document', 'Page',
      function($scope, Document, Page) {
        Page.setUp('Documents', 1);
        Page.selectedFiles = null;

        Document.get(function(response) {
          $scope.documents = response.documents;
        });
        
        $scope.saveFileLocally = function(selectedFiles) {
          Page.selectedFiles = selectedFiles;
        };
        
        $scope.uploadSelectedFile = function() {
          if (Page.selectedFiles != null) {
            // TODO this is where the import starts
            console.log(Page.selectedFiles[0]);
          } else {
            alert("Please select a document first.");
          }
        };
        
      }]);

})(angular);