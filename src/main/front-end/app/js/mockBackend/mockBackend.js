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
        $httpBackend.whenGET(new RegExp('/documents/[^/]+/chapters/1.1+')).respond(
                TestData.singleChapter);
        $httpBackend.whenGET(new RegExp('/documents/[^/]+/chapters/1.2+')).respond(
                TestData.secondChapter);
        $httpBackend.whenGET(new RegExp('/documents/[^/]+/chapters/1.3+')).respond(
                TestData.thirdChapter);

        $httpBackend.whenGET(new RegExp('/documents/[^/]+/places$')).respond(TestData.places);
        $httpBackend.whenGET(new RegExp('/documents/[^/]+/places/[^/]+$')).respond(
                TestData.singlePlace);
        $httpBackend.whenGET(new RegExp('/documents/[^/]+/entities/fingerprints[^/]+$')).respond(
                TestData.fingerprint);

        $httpBackend.whenGET(new RegExp('/documents/[^/]+/entities/[^/]+$')).respond(TestData.singlePerson);

        $httpBackend.whenGET(new RegExp('/documents/[^/]+/entities/relations/occurrences[^/]+$')).respond(
                TestData.relationOccurrences);

        $httpBackend.whenGET(new RegExp('/documents/[^/]+/persons$')).respond(TestData.persons);
        $httpBackend.whenGET(new RegExp('/documents/[^/]+/persons/[^/]+$')).respond(
                TestData.singlePerson);

        $httpBackend.whenGET(/\/documents\/[^/]+\/wordcloud\?entityId=person8Hugo$/).respond(
                TestData.wordcloudhugo);
        $httpBackend.whenGET(/\/documents\/[^/]+\/wordcloud\?entityId=person10Bert$/).respond(
                TestData.wordcloudbert);
        $httpBackend.whenGET(new RegExp('/documents/[^/]+/wordcloud$'))
                .respond(TestData.wordcloud);

      }]);
})(angular);
