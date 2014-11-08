(function(angular) {
  'use strict';

  var app = angular.module('vita');

  app.run([
      '$httpBackend',
      'TestData',
      function($httpBackend, TestData) {

        $httpBackend.whenGET(new RegExp('\.html$')).passThrough();
        $httpBackend.whenGET(new RegExp('/documents$')).respond(TestData.documents);
        $httpBackend.whenPOST(new RegExp('/documents$')).respond({documentId: 'testDocId'});

        $httpBackend.whenGET(new RegExp('/documents/[^/]+$')).respond(TestData.singleDocument);
        $httpBackend.whenDELETE(new RegExp('/documents/[^/]+$')).respond(undefined);

        $httpBackend.whenGET(new RegExp('/documents/[^/]+/progress$')).respond(
                TestData.analysisProgress);

        $httpBackend.whenGET(new RegExp('/documents/[^/]+/parts$')).respond(TestData.parts);

        $httpBackend.whenGET(new RegExp('/documents/[^/]+/places$')).respond(TestData.places);
        $httpBackend.whenGET(new RegExp('/documents/[^/]+/places/[^/]+$')).respond(
                TestData.singlePlace);
        $httpBackend.whenGET(new RegExp('/documents/[^/]+/entities/fingerprints[^/]+$')).respond(
                TestData.fingerprint);

        $httpBackend.whenGET(new RegExp('/documents/[^/]+/persons$')).respond(TestData.persons);
        $httpBackend.whenGET(new RegExp('/documents/[^/]+/persons/[^/]+$')).respond(
                TestData.singlePerson);

        $httpBackend.whenGET(new RegExp('/documents/[^/]+/wordcloud$')).respond(TestData.wordcloud);

      }]);
})(angular);
