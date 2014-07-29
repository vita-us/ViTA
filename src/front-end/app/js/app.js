'use strict';

var app = angular.module('vita', ['ngRoute']);

app.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/documents', {
    templateUrl: 'partials/documents.html',
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
