(function(angular) {
  'use strict';

  var app = angular.module('vita');

  app.run([
    '$httpBackend',
    function($httpBackend) {
      $httpBackend.whenGET(new RegExp('.*')).passThrough();
      $httpBackend.whenPOST(new RegExp('.*')).passThrough();
      $httpBackend.whenPUT(new RegExp('.*')).passThrough();
      $httpBackend.whenDELETE(new RegExp('.*')).passThrough();
    }]);
})(angular);
