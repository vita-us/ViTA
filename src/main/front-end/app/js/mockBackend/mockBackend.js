(function(angular) {
  'use strict';

  var app = angular.module('vita');

  app.run([
      '$httpBackend',
      'TestData',
      function($httpBackend, TestData) {

        $httpBackend.whenGET(new RegExp('\.html$')).passThrough();
        $httpBackend.whenGET(new RegExp('/documents$')).respond(TestData.documents);
        $httpBackend.whenGET(new RegExp('/documents/[^/]+$')).respond(TestData.singleDocument);

        $httpBackend.whenGET(new RegExp('/documents/[^/]+/places$')).respond(TestData.places);
        $httpBackend.whenGET(new RegExp('/documents/[^/]+/places/[^/]+$')).respond(
                TestData.singlePlace);
        
        $httpBackend.whenGET(new RegExp('/documents/[^/]+/characters$')).respond(TestData.characters);
        $httpBackend.whenGET(new RegExp('/documents/[^/]+/characters/[^/]+$')).respond(
                TestData.singleCharacter);

      }]);
})(angular);
