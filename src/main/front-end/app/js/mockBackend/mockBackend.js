(function(angular) {
  'use strict';

  var app = angular.module('vita');

  app.run([
      '$httpBackend',
      'TestData',
      function($httpBackend, TestData) {
        
        /*
         * except for fingerprint and occurrences of relations and attributes, all of the 
         * REST services originally mentioned should work now
         */
        $httpBackend.whenGET(new RegExp('/documents/[^/]+/[^/]+/fingerprints+$')).respond(
                TestData.fingerprint);

        $httpBackend.whenGET(new RegExp('.*')).passThrough();
      }]);
})(angular);
