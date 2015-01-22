(function(angular) {
  'use strict';

  var vitaControllers = angular.module('vitaControllers');

  vitaControllers.controller('GraphNetworkCtrl', ['$scope', '$routeParams', 'DocumentParts',
      'Document', 'Page', 'Person', 'CssClass', 'FingerprintSynchronizer',
      function($scope, $routeParams, DocumentParts, Document, Page, Person, CssClass, FingerprintSynchronizer) {

        $scope.loaded = false;

        // Provide the service for direct usage in the scope
        $scope.CssClass = CssClass;
        $scope.FingerprintSynchronizer = FingerprintSynchronizer;

        Document.get({
          documentId: $routeParams.documentId
        }, function(document) {
          Page.breadcrumbs = 'Graph-Network';
          Page.setUpForDocument(document);
        });

        Person.get({
          documentId: $routeParams.documentId
        }, function(response) {
          $scope.persons = response.persons;

          // manual initialization of the selection
          $scope.reset($scope.persons);
        });

        setGraphNetworkDimensions();
        $(window).resize(function() {
          setGraphNetworkDimensions();
          $scope.$apply();
        });

        function setGraphNetworkDimensions() {
          $scope.graphWidth = $('#graph-network-wrapper').width();
          $scope.graphHeight = $(window).height() * 0.7;
        }

        $scope.entities = [];

        var sliderMin = 0, sliderMax = 100;

        $('#slider-range').slider({
          range: true,
          min: sliderMin,
          max: sliderMax,
          values: [sliderMin, sliderMax],
          change: function(event, ui) {
            var start = ui.values[0], end = ui.values[1];

            setSliderLabel(start, end);

            $scope.rangeStart = start / 100;
            $scope.rangeEnd = end / 100;
            $scope.$apply();
          }
        });

        setSliderLabel(sliderMin, sliderMax);

        function setSliderLabel(start, end) {
          $('#amount').val(start + ' - ' + end);
        }

        $scope.loadGraphNetwork = function(person) {
          var position = $scope.entities.indexOf(person);
          if (position > -1) {
            $scope.entities.splice(position, 1);
          } else {
            $scope.entities.push(person);
          }
        };

        $scope.showFingerprint = function(ids) {
          $scope.fingerprintEntityIds = ids;
          Person.get({
            documentId: $routeParams.documentId,
            personId: ids[0]
          }, function(response) {
            $scope.fingerprintEntityA = response;
          });
          Person.get({
            documentId: $routeParams.documentId,
            personId: ids[1]
          }, function(response) {
            $scope.fingerprintEntityB = response;
          });
          $scope.$apply();
        };

        $scope.deselectAll = function() {
          $scope.entities = [];
        };

        $scope.reset = function(persons) {
          $scope.entities = persons.slice(0, 7);
        };

        $scope.isActive = function(person) {
          return ($scope.entities.indexOf(person) > -1);
        };

        DocumentParts.get({
          documentId: $routeParams.documentId
        }, function(response) {
          $scope.parts = response.parts;
        });
      }]);

})(angular);
