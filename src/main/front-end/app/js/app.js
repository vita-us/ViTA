(function(angular) {
  'use strict';

  var app = angular.module('vita', ['ngRoute', 'ngMockE2E', 'vitaControllers', 'vitaServices',
      'vitaDirectives']);

  angular.module('vitaControllers', []);
  angular.module('vitaServices', ['ngResource']);
  angular.module('vitaDirectives', []);

  app.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/documents', {
      templateUrl: 'partials/documents.html',
      controller: 'DocumentsCtrl'
    }).when('/settings', {
      templateUrl: 'partials/settings.html',
      controller: 'SettingsCtrl'
    }).when('/tutorial', {
      templateUrl: 'partials/tutorial.html',
      controller: 'TutorialCtrl'
    }).when('/about', {
      templateUrl: 'partials/about.html',
      controller: 'AboutCtrl'
    }).when('/documents/:documentId/overview', {
      templateUrl: 'partials/overview.html',
      controller: 'OverviewCtrl'
    }).when('/documents/:documentId/profiles', {
      templateUrl: 'partials/profiles.html',
      controller: ''
    }).when('/documents/:documentId/places', {
      templateUrl: 'partials/places.html',
      controller: 'PlaceListCtrl'
    }).when('/documents/:documentId/fingerprint', {
      templateUrl: 'partials/fingerprint.html',
      controller: ''
    }).when('/documents/:documentId/graphnetwork', {
      templateUrl: 'partials/graphnetwork.html',
      controller: ''
    }).when('/documents/:documentId/wordcloud', {
      templateUrl: 'partials/wordcloud.html',
      controller: ''
    }).when('/documents/:documentId/documentview', {
      templateUrl: 'partials/documentview.html',
      controller: ''
    }).when('/documents/:documentId/', {
      redirectTo: '/documents/:documentId/overview'
    }).otherwise({
      redirectTo: '/documents'
    });
  }]);

  app.factory('Page', function() {
    return {
      setUpForDocument: function(document) {
        // TODO Don't pass the id through the Page
        this.documentId = document.id;
        this.title = document.metadata.title;
        this.tab = 1;
        this.showMenu = true;
      },
      setUp: function(title, tab) {
        this.title = title;
        this.showMenu = false;
        this.tab = tab;
        this.breadcrumbs = null;
      }
    }
  });

  app.controller('PageCtrl', ['$scope', 'Page', function($scope, Page) {
    Page.title = 'Default page title';
    Page.breadcrumbs = null;
    Page.showMenu = true;
    Page.tab = 1;
    $scope.Page = Page;
  }]);

})(angular);
