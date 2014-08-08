(function(angular) {
  'use strict';

  var app = angular.module('vita', ['ngRoute', 'vitaControllers', 'vitaServices']);
  
  angular.module('vitaControllers', []);

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
      controller: ''
    }).when('/documents/:documentId/', {
      redirectTo: '/documents/:documentId/overview'
    }).otherwise({
      redirectTo: '/documents'
    });
  }]);

  app.factory('Page', function() {
    var title = "Default page title"; // set this in every controller
    var breadcrumbs = null;           // set this in those controllers where a document is selected
    var showMenu = true;              // set to false where no document is selected
    
    return {
      title: function() {
        return title;
      },
      setTitle: function(setTitle) {
        title = setTitle;
      },
      breadcrumbs: function() {
        return breadcrumbs;
      },
      setBreadcrumbs: function(setBreadcrumbs) {
        breadcrumbs = setBreadcrumbs;
      },
      showMenu: function() {
        return showMenu;
      },
      setShowMenu: function(setShowMenu) {
        showMenu = setShowMenu;
      }
    };
  });
 
  app.controller('PageCtrl', ['$scope', 'Page', function($scope, Page) {
    $scope.Page = Page;
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
