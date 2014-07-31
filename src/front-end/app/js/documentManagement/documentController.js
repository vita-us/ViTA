(function() {
  'use strict';
  
  var vitaControllers = angular.module('vitaControllers', []);

  // Controller responsible for the documents page
  vitaControllers.controller('DocumentsCtrl', ['$scope', 'Document', function($scope, Document) {
    var documentsObject = Document.get(function () {
		$scope.documents = documentsObject.documents;
	});
  }]);

})(angular);