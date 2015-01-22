(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  // Controller responsible for the places page
  vitaControllers.controller('PlaceListCtrl', ['$scope', 'Document', 'Page', 'Place',
      '$routeParams', function($scope, Document, Page, Place, $routeParams) {

        $scope.alternativeNames = function(place, searchQuery) {
          for (var i = 0; i < place.attributes.length; i++) {
            var attribute = place.attributes[i];
            if (attribute.attributetype == 'name' &&
              containsQueryCaseInsensitive(attribute.content, searchQuery)) {
              place.alternativeName = attribute.content;
            }
          }
        };

        var containsQueryCaseInsensitive = function(text, query) {
          var lowerCaseText = text.toLowerCase();
          var lowerCaseQuery = query.toLowerCase();

          return lowerCaseText.indexOf(lowerCaseQuery) > -1;
        };

        Place.get({
          documentId: $routeParams.documentId
        }, function(placesWrapper) {
          $scope.places = placesWrapper.places;
        });

        Document.get({
          documentId: $routeParams.documentId
        }, function(document) {
          $scope.document = document;
          Page.breadcrumbs = 'Places';
          Page.setUpForDocument(document);
        });
      }]);

})(angular);
