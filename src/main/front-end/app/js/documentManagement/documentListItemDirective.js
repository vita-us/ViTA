(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('documentListItem', function() {

    var directive = {
      restrict: 'A',
      scope: {
        document: '='
      },
      link: function(scope, element, attrs) {

        var progress = scope.document.progress;

        setStatusIconAndDescription(progress.status, scope);
        setOperationIconAndDescription(progress.status, scope);
      },
      templateUrl: 'templates/documentlistitem.html'
    };

    function setStatusIconAndDescription(status, scope) {
      switch (status) {
      case 'cancelled':
        scope.statusIconClass = 'glyphicon-remove-circle';
        scope.statusDescription = 'Analysis was cancelled';
        break;
      case 'failed':
        scope.statusIconClass = 'glyphicon-exclamation-sign';
        scope.statusDescription = 'Analysis has failed';
        break;
      case 'running':
        scope.statusIconClass = 'glyphicon-play-circle';
        scope.statusDescription = 'Analysis is running';
        break;
      case 'scheduled':
        scope.statusIconClass = 'glyphicon-time';
        scope.statusDescription = 'Analysis is scheduled';
        break;
      case 'success':
        scope.statusIconClass = 'glyphicon-ok-circle';
        scope.statusDescription = 'Analysis successed';
        break;
      }
    }

    function setOperationIconAndDescription(status, scope) {
      switch (status) {
      case 'cancelled':
      case 'failed':
        scope.operationIconClass = 'glyphicon-repeat';
        scope.operationDescription = 'Repeat analysis'
        break;
      case 'running':
      case 'scheduled':
        scope.operationIconClass = 'glyphicon-ban-circle';
        scope.operationDescription = 'Stop analysis'
        break;
      case 'success':
        // no operation
        break;
      }
    }

    return directive;
  });

})(angular);
