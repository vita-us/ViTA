(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the documents page
  vitaControllers.controller('DocumentsCtrl', ['$scope', 'Document', 'Page', 'DocumentViewSender',
      function($scope, Document, Page, DocumentViewSender) {
        Page.setUp('Documents', 1);

        Document.get(function(response) {
          $scope.documents = response.documents;
        });

        DocumentViewSender.sendTestMessage();
        DocumentViewSender.onReceive(function(data) {
          console.log(data);
        });
      }]);

})(angular);
