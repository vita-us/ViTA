(function(angular) {
  'use strict';

  var app = angular.module('vita', ['ngRoute', 'vitaControllers', 'vitaServices']);

  app.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/documents', {
      templateUrl: 'partials/documents.html',
<<<<<<< HEAD
      controller: 'DocumentsCtrl'

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
>>>>>>> basic-design
    }).when('/documents/:documentId/overview', {
      templateUrl: 'partials/overview.html',
      controller: ''
    }).when('/documents/:documentId/', {
      redirectTo: '/documents/:documentId/overview'
    }).otherwise({
      redirectTo: '/documents'
    });
  }]);

  app.controller("PanelController", function() {
    this.tab = 1;
    
    this.selectTab = function(setTab) {
      this.tab = setTab;
    };

    this.isSelected = function(checkTab) {
      return this.tab === checkTab;
    };
  });

})(angular);
