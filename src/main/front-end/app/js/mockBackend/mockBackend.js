(function(angular) {
  'use strict';

  var app = angular.module('vita');

  app.run([
      '$httpBackend',
      'TestData',
      function($httpBackend, TestData) {

        // fingerprint, occurrences of relations and attributes aren't working
        // currently
        $httpBackend.whenGET(new RegExp('webapi/documents/[^/]+/[^/]+/fingerprints+$')).respond(
                TestData.fingerprint);

        // implementation of the wordcloud is currently delayed
        $httpBackend.whenGET(new RegExp('webapi/documents/[^/]+/wordcloud$')).respond(
                TestData.wordcloud);

        $httpBackend.whenGET(new RegExp('.*')).passThrough();
        $httpBackend.whenPOST(new RegExp('.*')).passThrough();
        $httpBackend.whenPUT(new RegExp('.*')).passThrough();
        $httpBackend.whenDELETE(new RegExp('.*')).passThrough();
      }]);
})(angular);
