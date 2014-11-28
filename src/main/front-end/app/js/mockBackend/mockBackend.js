(function(angular) {
  'use strict';

  var app = angular.module('vita');

  app.run([
      '$httpBackend',
      'TestData',
      function($httpBackend, TestData) {

        // implementation of the wordcloud is currently delayed
        $httpBackend.whenGET(/\/documents\/[^/]+\/wordcloud\?entityId=person8Hugo$/).respond(
                TestData.wordcloudhugo);
        $httpBackend.whenGET(/\/documents\/[^/]+\/wordcloud\?entityId=person10Bert$/).respond(
                TestData.wordcloudbert);

        $httpBackend.whenGET(new RegExp('.*')).passThrough();
        $httpBackend.whenPOST(new RegExp('.*')).passThrough();
        $httpBackend.whenPUT(new RegExp('.*')).passThrough();
        $httpBackend.whenDELETE(new RegExp('.*')).passThrough();
      }]);
})(angular);
