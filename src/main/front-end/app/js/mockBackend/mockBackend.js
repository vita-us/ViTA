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

        $httpBackend.whenGET(new RegExp('/documents/[^/]+/progress$')).respond(
                TestData.analysisProgress);

        $httpBackend.whenGET(new RegExp('/documents/[^/]+/parts$')).respond(TestData.parts);

        $httpBackend.whenGET(new RegExp('/documents/[^/]+/places$')).respond(TestData.places);
        $httpBackend.whenGET(new RegExp('/documents/[^/]+/places/[^/]+$')).respond(
                TestData.singlePlace);

        $httpBackend.whenGET(new RegExp('/documents/[^/]+/[^/]+/fingerprints+$')).respond(
                TestData.fingerprint);

        $httpBackend.whenGET(new RegExp('/documents/[^/]+/persons$')).respond(TestData.persons);
        $httpBackend.whenGET(new RegExp('/documents/[^/]+/persons/[^/]+$')).respond(
                TestData.singlePerson);

        $httpBackend.whenGET(/\/documents\/[^/]+\/wordcloud\?entityId=person8Hugo$/).respond(TestData.wordcloudhugo);
        $httpBackend.whenGET(/\/documents\/[^/]+\/wordcloud\?entityId=person10Bert$/).respond(TestData.wordcloudbert);
        $httpBackend.whenGET(new RegExp('/documents/[^/]+/wordcloud$')).respond(TestData.wordcloud);

      }]);
})(angular);
