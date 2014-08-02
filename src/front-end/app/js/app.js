(function() {
  'use strict';

  var app = angular.module('vita', ['ngRoute']);

  app.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/documents', {
      templateUrl: 'partials/documents.html',
      controller: ''
    }).when('/settings', {
      templateUrl: 'partials/settings.html',
      controller: ''
    }).when('/tutorial', {
      templateUrl: 'partials/tutorial.html',
      controller: ''
    }).when('/about', {
      templateUrl: 'partials/about.html',
      controller: ''
    }).when('/documents/:documentId/overview', {
      templateUrl: 'partials/overview.html',
      controller: ''
    }).when('/documents/:documentId/', {
      redirectTo: '/documents/:documentId/overview'
    }).otherwise({
      redirectTo: '/documents'
    });
  }]);

})(angular);
