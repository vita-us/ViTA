(function(angular) {
  'use strict';

  var app = angular.module('vita', ['ngRoute', 'ngMockE2E', 'vitaControllers', 'vitaServices',
      'vitaDirectives', 'vitaFilters']);

  angular.module('vitaControllers', []);
  angular.module('vitaServices', ['ngResource']);
  angular.module('vitaDirectives', []);
  angular.module('vitaFilters', []);

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
    }).when('/documents/:documentId/characters', {
      templateUrl: 'partials/persons.html',
      controller: 'PersonListCtrl'
    }).when('/documents/:documentId/characters/:personId', {
      templateUrl: 'partials/persons.html',
      controller: 'PersonListCtrl'
    }).when('/documents/:documentId/places', {
      templateUrl: 'partials/places.html',
      controller: 'PlaceListCtrl'
    }).when('/documents/:documentId/places/:placeId', {
      templateUrl: 'partials/places.html',
      controller: 'PlaceListCtrl'
    }).when('/documents/:documentId/fingerprint', {
      templateUrl: 'partials/fingerprint.html',
      controller: 'FingerprintCtrl'
    }).when('/documents/:documentId/graphnetwork', {
      templateUrl: 'partials/graphnetwork.html',
      controller: 'GraphNetworkCtrl'
    }).when('/documents/:documentId/plotview', {
      templateUrl: 'partials/plotview.html',
      controller: 'PlotviewCtrl'
    }).when('/documents/:documentId/wordcloud', {
      templateUrl: 'partials/wordcloud.html',
      controller: 'WordcloudCtrl'
    }).when('/documents/:documentId/', {
      redirectTo: '/documents/:documentId/overview'
    }).otherwise({
      redirectTo: '/documents'
    });
  }]);

  app.factory('Page', ['DocumentViewSender', function(DocumentViewSender) {
            
    return {
      setUpForDocument: function(document) {
        var oldId = this.documentId;

        // TODO Don't pass the id through the Page
        this.documentId = document.id;
        this.title = document.metadata.title;
        this.tab = 1;
        this.showMenu = true;

        if (oldId !== document.id) {
          DocumentViewSender.sendDocumentId(document.id);
        }
      },
      setUp: function(title, tab) {
        this.title = title;
        this.showMenu = false;
        this.tab = tab;
        this.breadcrumbs = null;
      }
    };
  }]);

  app.controller('PageCtrl', [
      '$scope',
      'Page',
      'DocumentViewSender',
      '$routeParams',
      function($scope, Page, DocumentViewSender, $routeParams) {
        Page.title = 'Default page title';
        Page.breadcrumbs = null;
        Page.showMenu = true;
        Page.tab = 1;
        $scope.Page = Page;

        $scope.openDocumentView = DocumentViewSender.open;

        DocumentViewSender.onDocumentIdRequest(function() {
          DocumentViewSender.sendDocumentId($routeParams.documentId);
        });
      }]);

})(angular);
