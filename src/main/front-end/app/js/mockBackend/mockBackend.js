(function(angular) {
  'use strict';

  var app = angular.module('vita');

  app.run([
      '$httpBackend',
      'TestData',
      function($httpBackend, TestData) {
        $httpBackend.whenGET(new RegExp('/documents/[^/]+/plotview')).respond(TestData.plotviewData);

        $httpBackend.whenGET(new RegExp('.*')).passThrough();
        $httpBackend.whenPOST(new RegExp('.*')).passThrough();
        $httpBackend.whenPUT(new RegExp('.*')).passThrough();
        $httpBackend.whenDELETE(new RegExp('.*')).passThrough();
      }]);
})(angular);
