(function(angular) {
  'use strict';

  var app = angular.module('vita');

  app.run([
      '$httpBackend',
      'TestData',
      function($httpBackend, TestData) {

        $httpBackend.whenGET(new RegExp('\.html$')).passThrough();
        $httpBackend.whenGET(new RegExp('webapi/documents$')).respond(TestData.documents);
        $httpBackend.whenGET(new RegExp('webapi/documents/[^/]+$')).respond(TestData.singleDocument);

        $httpBackend.whenGET(new RegExp('webapi/documents/[^/]+/progress$')).respond(
                TestData.analysisProgress);

        $httpBackend.whenGET(new RegExp('webapi/documents/[^/]+/parts$')).respond(TestData.parts);

        $httpBackend.whenGET(new RegExp('webapi/documents/[^/]+/places$')).respond(TestData.places);
        $httpBackend.whenGET(new RegExp('webapi/documents/[^/]+/places/[^/]+$')).respond(
                TestData.singlePlace);

        $httpBackend.whenGET(new RegExp('webapi/documents/[^/]+/[^/]+/fingerprints+$')).respond(
                TestData.fingerprint);

        $httpBackend.whenGET(new RegExp('webapi/documents/[^/]+/persons$')).respond(TestData.persons);
        $httpBackend.whenGET(new RegExp('webapi/documents/[^/]+/persons/[^/]+$')).respond(
                TestData.singlePerson);

      }]);
})(angular);
